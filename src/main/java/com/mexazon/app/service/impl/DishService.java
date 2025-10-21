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

/**
 * Servicio para la gestión de platillos ({@link Dish}) dentro del menú de un negocio.
 * <p>
 * Proporciona operaciones CRUD con validaciones de integridad y unicidad
 * del nombre de platillo por negocio.
 */
@Service
public class DishService {

    private final DishRepository dishRepo;
    private final BusinessRepository businessRepo;
    private final MenuCategoryRepository categoryRepo;

    /**
     * Constructor del servicio de platillos.
     *
     * @param dishRepo Repositorio de platillos
     * @param businessRepo Repositorio de negocios
     * @param categoryRepo Repositorio de categorías del menú
     */
    public DishService(DishRepository dishRepo,
                       BusinessRepository businessRepo,
                       MenuCategoryRepository categoryRepo) {
        this.dishRepo = dishRepo;
        this.businessRepo = businessRepo;
        this.categoryRepo = categoryRepo;
    }

    // ---------- READ ----------
    /**
     * Lista los platillos de un negocio, con opción de filtrar por categoría.
     * <p>
     * Si {@code categoryId} es nulo, se devuelven todos los platillos del negocio.
     *
     * @param businessId ID del negocio
     * @param categoryId ID de la categoría (opcional)
     * @param pageable Objeto de paginación y orden
     * @return Página de platillos encontrados
     */
    @Transactional(readOnly = true)
    public Page<Dish> list(Long businessId, Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return dishRepo.findByBusiness_BusinessId(businessId, pageable);
        }
        return dishRepo.findByBusiness_BusinessIdAndCategory_CategoryId(businessId, categoryId, pageable);
    }

    // ---------- CREATE ----------
    /**
     * Crea un nuevo platillo para un negocio y categoría específicos.
     * <p>
     * Aplica las siguientes validaciones:
     * <ul>
     *   <li>El negocio y la categoría deben existir.</li>
     *   <li>El nombre y el precio del platillo son obligatorios.</li>
     *   <li>El precio debe ser mayor que cero.</li>
     *   <li>El nombre del platillo debe ser único dentro del negocio.</li>
     * </ul>
     *
     * @param businessId ID del negocio propietario
     * @param categoryId ID de la categoría a la que pertenece el platillo
     * @param dishName Nombre del platillo
     * @param description Descripción opcional
     * @param price Precio del platillo (mayor a 0)
     * @param photoUrl URL de la foto opcional
     * @return El platillo creado
     * @throws IllegalArgumentException si falta información o se violan las reglas de negocio
     */
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

        // Unicidad del nombre dentro del mismo negocio
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

    // ---------- UPDATE ----------
    /**
     * Actualiza un platillo existente.
     * <p>
     * Los campos nulos se ignoran (mantienen su valor actual).
     * Se validan los siguientes casos:
     * <ul>
     *   <li>Si el nombre cambia, se verifica que no esté duplicado dentro del negocio.</li>
     *   <li>El precio debe ser mayor que cero si se actualiza.</li>
     *   <li>La categoría debe existir si se especifica.</li>
     * </ul>
     *
     * @param dishId ID del platillo a actualizar
     * @param categoryId ID de la nueva categoría (opcional)
     * @param dishName Nuevo nombre (opcional)
     * @param description Nueva descripción (opcional)
     * @param price Nuevo precio (opcional)
     * @param photoUrl Nueva URL de la foto (opcional)
     * @return El platillo actualizado
     * @throws IllegalArgumentException si el platillo no existe o se violan validaciones
     */
    @Transactional
    public Dish update(Long dishId, Long categoryId, String dishName,
                       String description, Double price, String photoUrl) {

        Dish d = dishRepo.findById(dishId)
            .orElseThrow(() -> new IllegalArgumentException("Platillo no encontrado"));

        // Validación y actualización del nombre
        if (dishName != null && !dishName.isBlank()
                && !dishName.equalsIgnoreCase(d.getDishName())) {

            if (dishRepo.existsByBusiness_BusinessIdAndDishNameIgnoreCase(
                    d.getBusinessId(), dishName)) {
                throw new IllegalArgumentException("Nombre duplicado para ese negocio");
            }
            d.setDishName(dishName.trim());
        }

        if (description != null) d.setDescription(description);

        // Validación del precio
        if (price != null) {
            if (price <= 0.0)
                throw new IllegalArgumentException("price debe ser > 0");
            d.setPrice(price);
        }

        if (photoUrl != null) d.setPhotoUrl(photoUrl);

        // Actualización de categoría (si se especifica)
        if (categoryId != null) {
            MenuCategory cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            d.setCategoryId(cat);
        }

        return dishRepo.save(d);
    }

    // ---------- DELETE ----------
    /**
     * Elimina un platillo existente.
     *
     * @param dishId ID del platillo a eliminar
     * @throws IllegalArgumentException si el platillo no existe
     */
    @Transactional
    public void delete(Long dishId) {
        if (!dishRepo.existsById(dishId))
            throw new IllegalArgumentException("Platillo no encontrado");
        dishRepo.deleteById(dishId);
    }
}