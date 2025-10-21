package com.mexazon.app.controller;

import com.mexazon.app.model.Dish;
import com.mexazon.app.service.impl.DishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/businesses/{businessId}/dishes")
public class DishController {

    private final DishService service;

    public DishController(DishService service) { this.service = service; }

    // LIST (paginado, filtro opcional por categoría)
    @GetMapping
    public Page<Dish> list(@PathVariable Long businessId,
                           @RequestParam(required = false) Long categoryId,
                           @PageableDefault(size = 20, sort = "dishName") Pageable pageable) {
        return service.list(businessId, categoryId, pageable);
    }

    // CREATE
    @PostMapping
    public Dish create(@PathVariable Long businessId, @RequestBody Map<String, Object> body) {
        Long categoryId    = body.get("categoryId") == null ? null : Long.valueOf(body.get("categoryId").toString());
        String dishName    = (String) body.get("dishName");
        String description = (String) body.get("description");
        String photoUrl    = (String) body.get("photoUrl");

        // Convertimos a Double en lugar de BigDecimal
        Double price = null;
        if (body.get("price") != null) {
            try {
                price = Double.valueOf(body.get("price").toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El precio debe ser un número válido");
            }
        }

        return service.create(businessId, categoryId, dishName, description, price, photoUrl);
    }

    // UPDATE
    @PutMapping("/{dishId}")
    public Dish update(@PathVariable Long businessId, @PathVariable Long dishId, @RequestBody Map<String, Object> body) {
        Long categoryId    = body.get("categoryId") == null ? null : Long.valueOf(body.get("categoryId").toString());
        String dishName    = (String) body.get("dishName");
        String description = (String) body.get("description");
        String photoUrl    = (String) body.get("photoUrl");

        // Convertimos a Double en lugar de BigDecimal
        Double price = null;
        if (body.get("price") != null) {
            try {
                price = Double.valueOf(body.get("price").toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El precio debe ser un número válido");
            }
        }

        return service.update(dishId, categoryId, dishName, description, price, photoUrl);
    }


    // DELETE
    @DeleteMapping("/{dishId}")
    public void delete(@PathVariable Long businessId, @PathVariable Long dishId) {
        service.delete(dishId);
    }
}
