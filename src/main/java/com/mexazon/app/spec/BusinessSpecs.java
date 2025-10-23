package com.mexazon.app.spec;

import com.mexazon.app.model.UserAddress;
import com.mexazon.app.model.PostalCodeCatalog;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Dish;
import com.mexazon.app.model.MenuCategory;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public final class BusinessSpecs {

    private BusinessSpecs() {}

    public static Specification<Business> nameLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            var userJoin = root.join("user", JoinType.INNER);
            return cb.like(cb.lower(userJoin.get("name")), "%" + q.toLowerCase().trim() + "%");
        };
    }

    /**
     * Filtro por categorías (match ANY): si el negocio tiene al menos un Dish con una MenuCategory
     * cuyo slug o name esté en la lista normalizada (minúsculas, trim).
     *
     * Asume relaciones:
     *   Business { Set<Dish> dishes; }
     *   Dish { MenuCategory menuCategory; }
     *   MenuCategory { String slug; String name; }
     */
    public static Specification<Business> categoriesAnyViaDish(Collection<String> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) return cb.conjunction();

            List<String> normalized = categories.stream()
                    .filter(Objects::nonNull)
                    .map(s -> s.trim().toLowerCase())
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());

            if (normalized.isEmpty()) return cb.conjunction();

            Subquery<Integer> sq = query.subquery(Integer.class);
            Root<Dish> d = sq.from(Dish.class);
            var c = d.join("menuCategory", JoinType.INNER);

            Predicate bySlug = cb.lower(c.get("slug")).in(normalized);
            Predicate byName = cb.lower(c.get("name")).in(normalized);

            sq.select(cb.literal(1));
            sq.where(
                    cb.equal(d.get("business").get("businessId"), root.get("businessId")),
                    cb.or(bySlug, byName)
            );

            return cb.exists(sq);
        };
    }

    /** 
     * Filtra negocios por alcaldía usando la relación:
     * Business (businessId) == UserAddress(userId) -> catalogRef.alcaldia
     * No requiere que Business tenga un campo 'alcaldia'.
    */
    public static Specification<Business> alcaldiaEqViaUserAddress(String alcaldia) {
        return (root, query, cb) -> {
            if (alcaldia == null || alcaldia.isBlank()) return cb.conjunction();

            String target = alcaldia.trim().toLowerCase();

            // EXISTS (SELECT 1 FROM UserAddress ua JOIN ua.catalogRef pc
            //         WHERE ua.userId = business.businessId AND lower(pc.alcaldia) = :target)
            Subquery<Integer> sq = query.subquery(Integer.class);
            Root<UserAddress> ua = sq.from(UserAddress.class);
            var pc = ua.join("catalogRef", JoinType.INNER);

            sq.select(cb.literal(1));
            sq.where(
                cb.equal(ua.get("userId"), root.get("businessId")),
                cb.equal(cb.lower(pc.get("alcaldia")), target)
            );

            return cb.exists(sq);
        };
    }

}