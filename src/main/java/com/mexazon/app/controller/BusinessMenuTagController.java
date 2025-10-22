package com.mexazon.app.controller;

import com.mexazon.app.model.Dish;
import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.repository.DishRepository;
import com.mexazon.app.repository.MenuCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST que expone las categorías de menú efectivamente utilizadas
 * por un negocio en sus platillos registrados.
 * <p>
 * Este endpoint sirve para generar dinámicamente las "etiquetas de menú"
 * (tags) en el perfil del negocio, mostrando solo las categorías que tienen
 * al menos un platillo asociado.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Consultar los platillos de un negocio.</li>
 *   <li>Identificar las categorías únicas presentes en dichos platillos.</li>
 *   <li>Devolver una lista ordenada de categorías efectivamente utilizadas.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Usa {@link DishRepository} para obtener todos los platillos de un negocio.</li>
 *   <li>Usa {@link MenuCategoryRepository} para recuperar los nombres de categorías asociadas.</li>
 *   <li>Responde con una lista de objetos JSON simples con <code>categoryId</code> y <code>categoryName</code>.</li>
 *   <li>Ordena los resultados alfabéticamente por nombre de categoría.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * GET /api/businesses/5/menu/categories
 * </pre>
 *
 * <h3>Ejemplo de respuesta:</h3>
 * <pre>
 * 200 OK
 * [
 *   { "categoryId": 2, "categoryName": "Bebidas" },
 *   { "categoryId": 5, "categoryName": "Tacos" },
 *   { "categoryId": 8, "categoryName": "Postres" }
 * ]
 * </pre>
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessMenuTagController {

    /** Repositorio de platillos (para obtener categorías utilizadas). */
    private final DishRepository dishRepo;

    /** Repositorio de categorías (para traducir los IDs a nombres). */
    private final MenuCategoryRepository categoryRepo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param dishRepo repositorio de platillos
     * @param categoryRepo repositorio de categorías de menú
     */
    public BusinessMenuTagController(DishRepository dishRepo, MenuCategoryRepository categoryRepo) {
        this.dishRepo = dishRepo;
        this.categoryRepo = categoryRepo;
    }

    // ---------- GET /api/businesses/{businessId}/menu/categories ----------

    /**
     * Devuelve las categorías efectivamente usadas por un negocio.
     * <p>
     * Se filtran las categorías de todos los platillos activos del negocio
     * y se devuelven en orden alfabético, eliminando duplicados.
     * </p>
     *
     * @param businessId identificador del negocio
     * @return lista de categorías con <code>categoryId</code> y <code>categoryName</code>;
     *         vacía si el negocio no tiene platillos registrados.
     */
    @GetMapping("/{businessId}/menu/categories")
    public ResponseEntity<List<Map<String, Object>>> usedCategories(@PathVariable Long businessId) {
        // Obtener todos los platillos del negocio
        List<Dish> dishes = dishRepo.findByBusinessId(businessId);

        // Extraer IDs únicos de categorías
        Set<Long> categoryIds = dishes.stream()
                .map(Dish::getCategoryId)
                .collect(Collectors.toSet());

        if (categoryIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Recuperar las categorías por ID
        List<MenuCategory> categories = categoryRepo.findAllById(categoryIds);

        // Mapear a una estructura ligera y ordenada
        List<Map<String, Object>> result = categories.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("categoryId", c.getCategoryId());
                    map.put("categoryName", c.getCategoryName());
                    return map;
                })
                .sorted(Comparator.comparing(m -> m.get("categoryName").toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}