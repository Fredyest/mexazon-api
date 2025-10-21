package com.mexazon.app.controller;

import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.service.impl.MenuCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de categorías del menú ({@link MenuCategory}).
 *
 * Base path: {@code /api/menu-categories}
 *
 * Endpoints:
 * <ul>
 *   <li>Listar todas las categorías.</li>
 *   <li>Obtener categoría por ID.</li>
 *   <li>Crear una nueva categoría.</li>
 *   <li>Actualizar una categoría existente.</li>
 *   <li>Eliminar una categoría por ID.</li>
 * </ul>
 *
 * Notas:
 * - Las validaciones de negocio y actualizaciones de campos se realizan en {@link MenuCategoryService}.
 */
@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryController {

    private final MenuCategoryService service;

    public MenuCategoryController(MenuCategoryService service) {
        this.service = service;
    }

    // ---------- READ ----------
    /**
     * Lista todas las categorías del menú.
     *
     * Método/URL: GET /api/menu-categories
     *
     * Responses:
     * - 200 OK: lista de categorías
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/menu-categories"
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<MenuCategory>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Obtiene una categoría por su identificador.
     *
     * Método/URL: GET /api/menu-categories/{id}
     *
     * Path params:
     * - {@code id}: ID de la categoría
     *
     * Responses:
     * - 200 OK: categoría encontrada
     * - 404 Not Found: si no existe
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/menu-categories/3"
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuCategory> findById(@PathVariable Long id) {
        MenuCategory mc = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuCategory no encontrada con id " + id));
        return ResponseEntity.ok(mc);
    }

    // ---------- CREATE ----------
    /**
     * Crea una nueva categoría de menú.
     *
     * Método/URL: POST /api/menu-categories
     *
     * Body:
     * <pre>
     * {
     *   "categoryName": "Tacos"
     * }
     * </pre>
     *
     * Responses:
     * - 201 Created: categoría creada
     * - 400 Bad Request: validaciones fallidas (si las agregas en el servicio)
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/menu-categories" \
     *   -H "Content-Type: application/json" \
     *   -d '{"categoryName":"Tacos"}'
     * </pre>
     */
    @PostMapping
    public ResponseEntity<MenuCategory> create(@RequestBody CreateMenuCategoryRequest request) {
        MenuCategory entity = new MenuCategory();
        entity.setCategoryName(request.categoryName);
        // Agrega más setters si tu modelo tiene más campos

        MenuCategory saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza una categoría existente.
     * <p>
     * Solo se actualizan los campos provistos en el body, según implemente el servicio.
     *
     * Método/URL: PUT /api/menu-categories/{id}
     *
     * Path params:
     * - {@code id}: ID de la categoría a actualizar
     *
     * Body:
     * <pre>
     * {
     *   "categoryName": "Especialidades"
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: categoría actualizada
     * - 404 Not Found: categoría no encontrada
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT "http://localhost:8080/api/menu-categories/3" \
     *   -H "Content-Type: application/json" \
     *   -d '{"categoryName":"Especialidades"}'
     * </pre>
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuCategory> update(@PathVariable Long id,
                                               @RequestBody UpdateMenuCategoryRequest request) {
        MenuCategory updatedData = new MenuCategory();
        updatedData.setCategoryName(request.categoryName);
        // Agrega más setters si tu modelo tiene más campos

        MenuCategory updated = service.update(id, updatedData);
        return ResponseEntity.ok(updated);
    }

    // ---------- DELETE ----------
    /**
     * Elimina una categoría del menú por su identificador.
     *
     * Método/URL: DELETE /api/menu-categories/{id}
     *
     * Path params:
     * - {@code id}: ID de la categoría
     *
     * Responses:
     * - 204 No Content: categoría eliminada
     * - 404 Not Found: si no existe
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/menu-categories/3"
     * </pre>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ========= DTOs de request =========

    /**
     * Payload para crear una categoría de menú.
     */
    public static class CreateMenuCategoryRequest {
        /** Nombre de la categoría (ej. "Tacos", "Bebidas"). */
        public String categoryName;
    }

    /**
     * Payload para actualizar una categoría de menú.
     */
    public static class UpdateMenuCategoryRequest {
        /** Nuevo nombre de la categoría. */
        public String categoryName;
    }
}