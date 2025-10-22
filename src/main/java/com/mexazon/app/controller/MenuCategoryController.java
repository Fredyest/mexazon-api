package com.mexazon.app.controller;

import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.service.MenuCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la lectura del catálogo de categorías de menú.
 * <p>
 * Este endpoint permite al frontend obtener la lista completa de categorías
 * disponibles (por ejemplo: Tacos, Bebidas, Postres, etc.) para su uso en
 * formularios de registro de platillos o filtros en la vista de menú.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Proveer una lista completa y ordenada de categorías.</li>
 *   <li>Servir como catálogo estático accesible para el frontend.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link MenuCategoryService} como capa de negocio.</li>
 *   <li>No permite operaciones de creación, actualización ni eliminación;
 *       este catálogo se gestiona desde el backend.</li>
 *   <li>Responde siempre con código <strong>200 OK</strong>.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * GET /api/menu-categories
 * </pre>
 *
 * <h3>Ejemplo de respuesta:</h3>
 * <pre>
 * 200 OK
 * [
 *   { "categoryId": 1, "categoryName": "Tacos" },
 *   { "categoryId": 2, "categoryName": "Bebidas" },
 *   { "categoryId": 3, "categoryName": "Postres" }
 * ]
 * </pre>
 */
@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryController {

    /** Servicio de acceso al catálogo de categorías. */
    private final MenuCategoryService service;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param service servicio encargado de la gestión de categorías.
     */
    public MenuCategoryController(MenuCategoryService service) {
        this.service = service;
    }

    // ---------- GET /api/menu-categories ----------
    /**
     * Devuelve todas las categorías de menú disponibles en el sistema.
     *
     * @return {@code 200 OK} con la lista completa de {@link MenuCategory}.
     */
    @GetMapping
    public ResponseEntity<List<MenuCategory>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}