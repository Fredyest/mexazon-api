package com.mexazon.app.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.BusinessHours;
import com.mexazon.app.model.BusinessHoursId;
import com.mexazon.app.repository.BusinessHoursRepository;

@Service
@Transactional
public class BusinessHoursService {

    private final BusinessHoursRepository repository;

    public BusinessHoursService(BusinessHoursRepository repository) {
        this.repository = repository;
    }

    // ---------- CREATE ----------
    public BusinessHours create(Business business, int dayOfWeek,
                                LocalTime timeIn, LocalTime timeOut, boolean isWorking) {

        if (isWorking && timeIn != null && timeOut != null && !timeOut.isAfter(timeIn)) {
            throw new IllegalArgumentException("time_out debe ser posterior a time_in");
        }

        BusinessHoursId id = new BusinessHoursId(business.getBusinessId(), dayOfWeek);
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Ya existe horario para ese negocio y día");
        }

        BusinessHours bh = new BusinessHours();
        bh.setId(id);
        bh.setBusiness(business);
        bh.setWorking(isWorking);
        bh.setTimeIn(timeIn);
        bh.setTimeOut(timeOut);

        return repository.save(bh);
    }

    // ---------- READ ----------
    @Transactional(readOnly = true)
    public BusinessHours get(Long businessId, int dayOfWeek) {
        return repository.findById(new BusinessHoursId(businessId, dayOfWeek))
            .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<BusinessHours> getAllByBusiness(Business business) {
        return repository.findByBusiness(business);
    }

    // ---------- UPDATE ----------
    public BusinessHours update(Long businessId, int dayOfWeek,
                                LocalTime timeIn, LocalTime timeOut, Boolean isWorking) {

        BusinessHours existing = get(businessId, dayOfWeek);

        if (isWorking != null) existing.setWorking(isWorking);
        if (timeIn != null) existing.setTimeIn(timeIn);
        if (timeOut != null) existing.setTimeOut(timeOut);

        if (existing.isWorking()
                && existing.getTimeIn() != null
                && existing.getTimeOut() != null
                && !existing.getTimeOut().isAfter(existing.getTimeIn())) {
            throw new IllegalArgumentException("time_out debe ser posterior a time_in");
        }

        return repository.save(existing);
    }

    // Marcar como cerrado 
    public BusinessHours close(Long businessId, int dayOfWeek) {
        BusinessHours existing = get(businessId, dayOfWeek);
        existing.setWorking(false);
        existing.setTimeIn(null);
        existing.setTimeOut(null);
        return repository.save(existing);
    }

    // ---------- DELETE ----------
    public void delete(Long businessId, int dayOfWeek) {
        BusinessHoursId id = new BusinessHoursId(businessId, dayOfWeek);
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Horario no encontrado");
        }
        repository.deleteById(id);
    }
}

