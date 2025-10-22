package com.mexazon.app.controller;

import com.mexazon.app.model.Dish;
import com.mexazon.app.service.DishService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestionar los platillos ({@link Dish}) de los negocios.
 * <p>
 * Expone endpoints para crear y consultar platillos.
 * Por diseño actual, no incluye operaciones de actualización ni eliminación
 * desde el frontend, manteniendo el flujo de gestión controlado por el backend.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Registrar nuevos platillos con validaciones de precio y duplicados.</li>
 *   <li>Consultar un platillo específico por su ID.</li>
 *   <li>Listar platillos filtrados por negocio, categoría, texto y rango de precios.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link DishService} para encapsular la lógica de negocio.</li>
 *   <li>Incluye manejo explícito de errores comunes:
 *     <ul>
 *       <li><strong>400 Bad Request</strong>: precio inválido o datos incorrectos.</li>
 *       <li><strong>409 Conflict</strong>: nombre duplicado dentro del mismo negocio.</li>
 *       <li><strong>404 Not Found</strong>: platillo inexistente.</li>
 *     </ul>
 *   </li>
 *   <li>Los filtros de búsqueda son opcionales y combinables en el endpoint de listado.</li>
 * </ul>
 *
 * <h3>Ejemplos de uso:</h3>
 *
 * <h4>➤ Crear platillo</h4>
 * <pre>
 * POST /api/dishes
 * Content-Type: application/json
 * {
 *   "businessId": 3,
 *   "categoryId": 1,
 *   "dishName": "Taco al pastor",
 *   "description": "Tacos con piña y cebolla",
 *   "price": 45.00,
 *   "photoUrl": "https://cdn..."
 * }
 * </pre>
 * <h4>Respuesta exitosa</h4>
 * <pre>
 * 201 Created
 * {
 *   "dishId": 12,
 *   "businessId": 3,
 *   "categoryId": 1,
 *   "dishName": "Taco al pastor",
 *   "price": 45.00,
 *   "photoUrl": "https://cdn..."
 * }
 * </pre>
 *
 * <h4>➤ Consultar platillo</h4>
 * <pre>
 * GET /api/dishes/12
 * </pre>
 *
 * <h4>➤ Listar platillos filtrados</h4>
 * <pre>
 * GET /api/dishes?businessId=3&categoryId=1&search=taco&minPrice=20&maxPrice=60
 * </pre>
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    /** Servicio encargado de la gestión de platillos. */
    private final DishService service;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param service servicio de negocio para platillos.
     */
    public DishController(DishService service) {
        this.service = service;
    }

    // ---------- POST /api/dishes ----------
    /**
     * Crea un nuevo platillo en el sistema.
     * <p>
     * Valida que el precio sea mayor o igual a 0 y que el nombre del platillo
     * no esté duplicado dentro del mismo negocio.
     * </p>
     *
     * @param newDish entidad {@link Dish} enviada desde el cliente.
     * @return {@code 201 Created} si se registró correctamente;
     *         {@code 400 Bad Request} si los datos son inválidos;
     *         {@code 409 Conflict} si el nombre del platillo ya existe.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Dish newDish) {
        try {
            Dish created = service.create(newDish);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    java.util.Map.of(
                            "status", 400,
                            "error", "Bad Request",
                            "message", e.getMessage()
                    )
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    java.util.Map.of(
                            "status", 409,
                            "error", "Conflict",
                            "message", "Dish name already exists for this business"
                    )
            );
        }
    }

    // ---------- GET /api/dishes/{dishId} ----------
    /**
     * Recupera un platillo por su identificador único.
     *
     * @param dishId identificador del platillo.
     * @return {@code 200 OK} con el platillo si existe,
     *         o {@code 404 Not Found} si no fue encontrado.
     */
    @GetMapping("/{dishId}")
    public ResponseEntity<Dish> getById(@PathVariable Long dishId) {
        return service.getById(dishId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ---------- GET /api/dishes ----------
    /**
     * Lista los platillos de un negocio, aplicando filtros opcionales.
     * <p>
     * Soporta los siguientes parámetros:
     * <ul>
     *   <li><b>businessId</b> (requerido): ID del negocio propietario.</li>
     *   <li><b>categoryId</b> (opcional): ID de categoría de menú.</li>
     *   <li><b>search</b> (opcional): texto de búsqueda parcial en nombre o descripción.</li>
     *   <li><b>minPrice</b> y <b>maxPrice</b> (opcionales): rango de precios.</li>
     * </ul>
     * </p>
     *
     * @param businessId ID del negocio (obligatorio)
     * @param categoryId ID de la categoría (opcional)
     * @param search texto de búsqueda parcial (opcional)
     * @param minPrice precio mínimo (opcional)
     * @param maxPrice precio máximo (opcional)
     * @return {@code 200 OK} con la lista de platillos filtrados.
     */
    @GetMapping
    public ResponseEntity<List<Dish>> list(
            @RequestParam Long businessId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<Dish> result = service.listByBusiness(businessId, categoryId, search, minPrice, maxPrice);
        return ResponseEntity.ok(result);
    }
}