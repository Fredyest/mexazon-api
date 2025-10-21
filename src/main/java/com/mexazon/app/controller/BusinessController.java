package com.mexazon.app.controller;

import com.mexazon.app.model.Business;
import com.mexazon.app.service.impl.BusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de negocios ({@link Business}).
 * <p>
 * Base path: {@code /api/businesses}
 * <br>
 * Proporciona endpoints para:
 * <ul>
 *   <li>Crear un negocio asociado a un usuario (idempotente).</li>
 *   <li>Activar o desactivar un negocio existente.</li>
 *   <li>Consultar un negocio por su identificador.</li>
 * </ul>
 *
 * Autenticación/Autorización: (definir si aplica; p. ej., rol ADMIN o propietario del negocio)
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    private final BusinessService service;

    public BusinessController(BusinessService service) {
        this.service = service;
    }

    // ---------- CREATE ----------
    /**
     * Crea el negocio asociado a un usuario de tipo {@code "business"}.
     * <p>
     * Si el usuario ya tiene un negocio registrado, se devuelve el existente (operación idempotente).
     *
     * Método/URL: POST /api/businesses?userId={userId}
     * Parámetros:
     * - {@code userId}: ID del usuario que será propietario del negocio
     *
     * Responses:
     * - 201 Created: negocio creado o recuperado correctamente
     * - 400 Bad Request: usuario no válido o no es de tipo "business"
     * - 404 Not Found: usuario inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/businesses?userId=5"
     * </pre>
     *
     * @param userId ID del usuario propietario
     * @return El negocio creado o existente
     */
    @PostMapping
    public ResponseEntity<Business> create(@RequestParam("userId") Long userId) {
        Business created = service.createFromUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- UPDATE ----------
    /**
     * Activa o desactiva un negocio existente.
     * <p>
     * Si no se envía el campo {@code active} en el body, se asume {@code true}.
     *
     * Método/URL: PATCH /api/businesses/{id}/active
     * Path params:
     * - {@code id}: ID del negocio
     *
     * Body:
     * <pre>
     * {
     *   "active": false
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: negocio actualizado
     * - 404 Not Found: negocio inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X PATCH "http://localhost:8080/api/businesses/5/active" \
     *   -H "Content-Type: application/json" \
     *   -d '{"active": false}'
     * </pre>
     *
     * @param businessId ID del negocio a modificar
     * @param body Objeto con el estado activo/inactivo
     * @return El negocio actualizado
     */
    @PatchMapping("/{id}/active")
    public Business setActive(@PathVariable("id") Long businessId, @RequestBody ActiveDto body) {
        boolean active = (body != null && body.active != null) ? body.active : true;
        return service.toggleActive(businessId, active);
    }

    // ---------- READ ----------
    /**
     * Obtiene un negocio por su identificador (equivalente al {@code userId} del propietario).
     *
     * Método/URL: GET /api/businesses/{id}
     * Path params:
     * - {@code id}: ID del negocio
     *
     * Responses:
     * - 200 OK: negocio encontrado
     * - 404 Not Found: negocio no existente
     *
     * Ejemplo:
     * <pre>
     * curl http://localhost:8080/api/businesses/5
     * </pre>
     *
     * @param businessId ID del negocio
     * @return El negocio correspondiente
     */
    @GetMapping("/{id}")
    public Business get(@PathVariable("id") Long businessId) {
        return service.get(businessId);
    }

    // ---------- DTOs ----------
    /**
     * DTO mínimo para la actualización del estado activo/inactivo de un negocio.
     */
    public static class ActiveDto {
        /** Indica si el negocio debe estar activo o inactivo. */
        public Boolean active;
    }
}