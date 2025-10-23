package com.mexazon.app.spec;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Dish;
import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.model.UserAddress;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public final class BusinessSpecs {

    private BusinessSpecs() {}

    /** Buscar por nombre del usuario dueño del negocio (Business -> User.name) */
    public static Specification<Business> nameLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            return cb.like(
                cb.lower(root.join("user", JoinType.INNER).get("name")),
                "%" + q.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * Filtra negocios que tengan al menos un Dish cuya categoría (por nombre)
     * esté en la lista recibida (match ANY).
     *
     * NOTA: Dish y MenuCategory no tienen @ManyToOne en tu modelo; enlazamos por IDs
     * usando una subquery EXISTS.
     */
	public static Specification<Business> categoriesAny(Collection<String> categories) {
	    return (root, query, cb) -> {
	        if (categories == null || categories.isEmpty()) return cb.conjunction();
	
	        List<String> normalized = categories.stream()
	                .filter(Objects::nonNull)
	                .map(s -> s.trim().toLowerCase())
	                .filter(s -> !s.isBlank())
	                .toList();
	
	        if (normalized.isEmpty()) return cb.conjunction();
	
	        Subquery<Long> sq = query.subquery(Long.class);
	        Root<Dish> d = sq.from(Dish.class);
	        Root<MenuCategory> mc = sq.from(MenuCategory.class);
	
	        sq.select(cb.literal(1L));
	        sq.where(
	            cb.equal(d.get("businessId"), root.get("businessId")),
	            cb.equal(d.get("categoryId"), mc.get("categoryId")),
	            cb.lower(mc.get("categoryName")).in(normalized)
	        );
	
	        // ⚠️ QUITAR esta línea:
	        // query.distinct(true);
	
	        return cb.exists(sq);
	    };
	}


    /**
     * Alias para compatibilidad con tu servicio actual.
     * (El servicio llama a categoriesAnyViaDish; delegamos al método real.)
     */
    public static Specification<Business> categoriesAnyViaDish(Collection<String> categories) {
        return categoriesAny(categories);
    }

    /**
     * Filtra por alcaldía usando la relación:
     * Business.businessId == UserAddress.userId  ->  UserAddress.catalogRef.alcaldia
     * (No requiere campo 'alcaldia' en Business)
     */
    public static Specification<Business> alcaldiaEqViaUserAddress(String alcaldia) {
        return (root, query, cb) -> {
            if (alcaldia == null || alcaldia.isBlank()) return cb.conjunction();

            String target = alcaldia.trim().toLowerCase();

            // EXISTS (
            //   SELECT 1 FROM UserAddress ua JOIN ua.catalogRef pc
            //   WHERE ua.userId = business.businessId
            //     AND lower(pc.alcaldia) = :target
            // )
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