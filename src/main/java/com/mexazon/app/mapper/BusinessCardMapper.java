package com.mexazon.app.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mexazon.app.dto.BusinessCard;
import com.mexazon.app.model.Business;
import com.mexazon.app.model.User;

@Component
public class BusinessCardMapper {

    public BusinessCard toCard(Business b) {
        if (b == null) return null;

        // Business solo tiene businessId, user, isActive
        User u = b.getUser();

        String name = (u != null && u.getName() != null)
                ? u.getName()
                : ("Negocio #" + (b.getBusinessId() != null ? b.getBusinessId() : ""));
        String avatarUrl = (u != null) ? u.getAvatarUrl() : null;

        // De momento, sin campos en Business para rating/resenas -> 0 por default
        Long reviews = 0L;
        Double rating = 0.0;

        return new BusinessCard(
                b.getBusinessId(),   // <-- usa businessId
                name,
                avatarUrl,
                reviews,
                rating
        );
    }

    public List<BusinessCard> toCards(Collection<Business> businesses) {
        if (businesses == null) return List.of();
        return businesses.stream()
                .filter(Objects::nonNull)
                .map(this::toCard)
                .collect(Collectors.toList());  // <- ojo: toList() con L mayúscula
    }
}