package com.mexazon.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mexazon.app.dto.BusinessCard;
import com.mexazon.app.mapper.BusinessCardMapper;
import com.mexazon.app.model.Business;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.UserAddressRepository;
import com.mexazon.app.spec.BusinessSpecs;

@Service
public class BusinessSearchService {

    private final BusinessRepository businessRepo;
    private final UserAddressRepository userAddressRepo;
    private final BusinessCardMapper mapper;

    public BusinessSearchService(
            BusinessRepository businessRepo,
            UserAddressRepository userAddressRepo,
            BusinessCardMapper mapper
    ) {
        this.businessRepo = businessRepo;
        this.userAddressRepo = userAddressRepo;
        this.mapper = mapper;
    }

    // --------- Búsqueda general (q + alcaldia + categories ANY) ---------
    public Page<BusinessCard> search(String q, String alcaldia, List<String> categories,
                                     Integer page, Integer size, String sortParam) {

        Specification<Business> spec = Specification
            .where(BusinessSpecs.nameLike(q))
            .and(BusinessSpecs.alcaldiaEqViaUserAddress(alcaldia))
            .and(BusinessSpecs.categoriesAnyViaDish(categories));

        Pageable pageable = buildPageable(page, size, sortParam, "name,asc");

        return businessRepo.findAll(spec, pageable).map(mapper::toCard);
    }

    public Page<BusinessCard> topInUserAlcaldia(Long userId, Integer page, Integer size) {
        String alcaldia = userAddressRepo.findById(userId)
                .map(ua -> ua.getCatalogRef() != null ? ua.getCatalogRef().getAlcaldia() : null)
                .orElse(null);

        if (alcaldia == null || alcaldia.isBlank()) return Page.empty();

        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 20 : Math.max(Math.min(size, 50), 1);
        Pageable pageable = PageRequest.of(p, s); // ORDER BY viene de la consulta del repo

        return businessRepo.findTopByAlcaldiaOrderByAvgRatingDesc(alcaldia, pageable);
    }

    // --------- Helpers ---------
    private Pageable buildPageable(Integer page, Integer size, String sortParam, String defaultSort) {
        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 20 : Math.max(Math.min(size, 50), 1);
        String use = (sortParam == null || sortParam.isBlank()) ? defaultSort : sortParam;

        String[] parts = use.split(",");
        String field = parts[0].trim();
        boolean desc = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc");
        Sort sort = desc ? Sort.by(field).descending() : Sort.by(field).ascending();

        return PageRequest.of(p, s, sort);
    }

}