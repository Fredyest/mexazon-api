package com.mexazon.app.controller;

import com.mexazon.app.model.Dish;
import com.mexazon.app.service.impl.DishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la gestión de platillos ({@link Dish}) de un negocio.
 *
 * Base path: {@code /api/businesses/{businessId}/dishes}
 * Entidad principal: Platillo perteneciente a un negocio concreto.
 *
 * Notas:
 * - Soporta paginación y ordenamiento vía Spring Data (parámetros {@code page}, {@code size}, {@code sort}).
 * - El precio se procesa como {@link Double} desde el body (validación de formato incluida).
 * - La creación/actualización valida nombre, precio y unicidad por negocio (en la capa de servicio).
 */
@RestController
@RequestMapping("/api/businesses/{businessId}/dishes")
public class DishController {

    private final DishService service;

    public DishController(DishService service) { this.service = service; }

    // ---------- READ ----------
    /**
     * Lista los platillos de un negocio con paginación y filtro opcional por categoría.
     *
     * Método/URL: GET /api/businesses/{businessId}/dishes
     *
     * Path params:
     * - {@code businessId}: ID del negocio propietario de los platillos
     *
     * Query params (opcionales):
     * - {@code categoryId}: ID de la categoría para filtrar
     * - Paginación/orden: {@code page} (0..N), {@code size}, {@code sort} (p. ej. {@code sort=dishName,asc})
     *
     * Responses:
     * - 200 OK: página de platillos
     * - 404 Not Found: si el negocio no existe (validado indirectamente en el servicio/repo al crear/usar filtros)
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/businesses/1/dishes?categoryId=3&page=0&size=20&sort=dishName,asc"
     * </pre>
     */
    @GetMapping
    public Page<Dish> list(@PathVariable Long businessId,
                           @RequestParam(required = false) Long categoryId,
                           @PageableDefault(size = 20, sort = "dishName") Pageable pageable) {
        return service.list(businessId, categoryId, pageable);
    }

    // ---------- CREATE ----------
    /**
     * Crea un nuevo platillo para un negocio.
     *
     * Método/URL: POST /api/businesses/{businessId}/dishes
     *
     * Path params:
     * - {@code businessId}: ID del negocio propietario
     *
     * Body:
     * <pre>
     * {
     *   "categoryId": 3,            // obligatorio
     *   "dishName": "Taco al pastor", // obligatorio
     *   "description": "Con piña",
     *   "price": 25.5,              // > 0 (Double)
     *   "photoUrl": "https://..."
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: platillo creado (el servicio retorna la entidad persistida)
     * - 400 Bad Request: validaciones (nombre vacío, price <= 0, formato price inválido, categoría faltante, duplicado por negocio)
     * - 404 Not Found: negocio o categoría inexistentes
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/businesses/1/dishes" \
     *   -H "Content-Type: application/json" \
     *   -d '{"categoryId":3,"dishName":"Taco al pastor","description":"Con piña","price":25.5,"photoUrl":"https://..."}'
     * </pre>
     */
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

    // ---------- UPDATE ----------
    /**
     * Actualiza un platillo existente (actualización parcial permitida).
     *
     * Método/URL: PUT /api/businesses/{businessId}/dishes/{dishId}
     *
     * Path params:
     * - {@code businessId}: ID del negocio propietario (contexto de la ruta)
     * - {@code dishId}: ID del platillo a actualizar
     *
     * Body (campos opcionales; si un campo es null/no viene, se conserva):
     * <pre>
     * {
     *   "categoryId": 4,
     *   "dishName": "Taco de suadero",
     *   "description": "Con salsa verde",
     *   "price": 28.0,
     *   "photoUrl": "https://..."
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: platillo actualizado
     * - 400 Bad Request: validaciones (precio <= 0, nombre duplicado en el mismo negocio, formato precio inválido)
     * - 404 Not Found: platillo o categoría inexistentes
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT "http://localhost:8080/api/businesses/1/dishes/12" \
     *   -H "Content-Type: application/json" \
     *   -d '{"dishName":"Taco de suadero","price":28.0}'
     * </pre>
     */
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

    // ---------- DELETE ----------
    /**
     * Elimina un platillo por su identificador.
     *
     * Método/URL: DELETE /api/businesses/{businessId}/dishes/{dishId}
     *
     * Path params:
     * - {@code businessId}: ID del negocio (contexto)
     * - {@code dishId}: ID del platillo a eliminar
     *
     * Responses:
     * - 200 OK / 204 No Content: eliminado (según configuración)
     * - 404 Not Found: platillo inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/businesses/1/dishes/12"
     * </pre>
     */
    @DeleteMapping("/{dishId}")
    public void delete(@PathVariable Long businessId, @PathVariable Long dishId) {
        service.delete(dishId);
    }
}