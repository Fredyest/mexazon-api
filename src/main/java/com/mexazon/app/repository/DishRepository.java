package com.mexazon.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Dish;
import com.mexazon.app.model.MenuCategory;

import java.util.List;
@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    // --- ya existentes ---
    List<Dish> findAllByCategory(MenuCategory category);
    List<Dish> findAllByBusiness(Business business);
    List<Dish> findAllByCategoryCategoryId(Long categoryId);
    List<Dish> findAllByBusinessBusinessId(Long businessId);

    // --- nuevos métodos (para DishService) ---
    
    // Paginado por negocio
    org.springframework.data.domain.Page<Dish> findByBusiness_BusinessId(Long businessId, org.springframework.data.domain.Pageable pageable);

    // Paginado por negocio + categoría
    org.springframework.data.domain.Page<Dish> findByBusiness_BusinessIdAndCategory_CategoryId(Long businessId, Long categoryId, org.springframework.data.domain.Pageable pageable);

    // Validación de unicidad: no permitir nombres repetidos en el mismo negocio
    boolean existsByBusiness_BusinessIdAndDishNameIgnoreCase(Long businessId, String dishName);
}