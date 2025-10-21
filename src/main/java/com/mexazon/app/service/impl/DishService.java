package com.mexazon.app.service.impl;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Dish;
import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.DishRepository;
import com.mexazon.app.repository.MenuCategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DishService {

    private final DishRepository dishRepo;
    private final BusinessRepository businessRepo;
    private final MenuCategoryRepository categoryRepo;

    public DishService(DishRepository dishRepo,
                       BusinessRepository businessRepo,
                       MenuCategoryRepository categoryRepo) {
        this.dishRepo = dishRepo;
        this.businessRepo = businessRepo;
        this.categoryRepo = categoryRepo;
    }

    // -------- LIST --------
    @Transactional(readOnly = true)
    public Page<Dish> list(Long businessId, Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return dishRepo.findByBusiness_BusinessId(businessId, pageable);
        }
        return dishRepo.findByBusiness_BusinessIdAndCategory_CategoryId(businessId, categoryId, pageable);
    }

    // -------- CREATE --------
    @Transactional
    public Dish create(Long businessId, Long categoryId, String dishName,
                       String description, Double price, String photoUrl) {

        Business biz = businessRepo.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));

        if (categoryId == null)
            throw new IllegalArgumentException("categoryId es obligatorio");
        MenuCategory cat = categoryRepo.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (dishName == null || dishName.isBlank())
            throw new IllegalArgumentException("dishName es obligatorio");

        if (price == null || price <= 0.0)
            throw new IllegalArgumentException("price debe ser > 0");

        // Unicidad nombre dentro del negocio
        if (dishRepo.existsByBusiness_BusinessIdAndDishNameIgnoreCase(businessId, dishName))
            throw new IllegalArgumentException("Ya existe un platillo con ese nombre en el negocio");

        Dish d = new Dish();
        d.setBusinessId(biz);
        d.setCategoryId(cat);
        d.setDishName(dishName.trim());
        d.setDescription(description);
        d.setPrice(price);
        d.setPhotoUrl(photoUrl);

        return dishRepo.save(d);
    }

    // -------- UPDATE --------
    @Transactional
    public Dish update(Long dishId, Long categoryId, String dishName,
                       String description, Double price, String photoUrl) {

        Dish d = dishRepo.findById(dishId)
            .orElseThrow(() -> new IllegalArgumentException("Platillo no encontrado"));

        // Nombre (validar duplicado si cambia)
        if (dishName != null && !dishName.isBlank()
                && !dishName.equalsIgnoreCase(d.getDishName())) {
            if (dishRepo.existsByBusiness_BusinessIdAndDishNameIgnoreCase(
                    d.getBusinessId(), dishName)) {
                throw new IllegalArgumentException("Nombre duplicado para ese negocio");
            }
            d.setDishName(dishName.trim());
        }

        if (description != null) d.setDescription(description);

        if (price != null) {
            if (price <= 0.0) throw new IllegalArgumentException("price debe ser > 0");
            d.setPrice(price);
        }

        if (photoUrl != null) d.setPhotoUrl(photoUrl);

        if (categoryId != null) {
            MenuCategory cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            d.setCategoryId(cat);
        }

        return dishRepo.save(d);
    }

    // -------- DELETE --------
    @Transactional
    public void delete(Long dishId) {
        if (!dishRepo.existsById(dishId))
            throw new IllegalArgumentException("Platillo no encontrado");
        dishRepo.deleteById(dishId);
    }
}

