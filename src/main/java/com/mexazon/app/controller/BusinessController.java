package com.mexazon.app.controller;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.BusinessHour;
import com.mexazon.app.service.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST para gestionar negocios y sus horarios en Mexazón.
 * <p>
 * Expone endpoints para crear negocios con sus horarios, actualizar
 * información visible (nombre, descripción y avatar), consultar un negocio
 * y obtener sus horarios de operación.
 * </p>
 *
 * <h3>Notas de diseño</h3>
 * <ul>
 *   <li>La creación recibe un payload flexible (Map) para mantener el controlador delgado.
 *       En el futuro puede migrarse a DTOs tipados.</li>
 *   <li>Los horarios se aceptan como lista simple y se transforman a {@link BusinessHour}.</li>
 *   <li>Respuestas con <strong>HTTP 201</strong> al crear; <strong>HTTP 200</strong> en lecturas/updates;
 *       <strong>HTTP 404</strong> si el recurso no existe.</li>
 * </ul>
 *
 * <h3>Payload de creación</h3>
 * <pre>
 * POST /api/businesses
 * Content-Type: application/json
 * {
 *   "businessId": 10,
 *   "isActive": true,
 *   "businessHours": [
 *     {"dayOfWeek":"Mon","timeIn":"09:00","timeOut":"18:00","isWorking":true},
 *     {"dayOfWeek":"Tue","timeIn":"09:00","timeOut":"18:00","isWorking":true}
 *   ]
 * }
 * </pre>
 *
 * <h3>Respuesta de consulta</h3>
 * <pre>
 * 200 OK
 * {
 *   "businessId": 10,
 *   "isActive": true,
 *   "user": {
 *     "name": "Taquería Don Pepe",
 *     "description": "Tacos al pastor",
 *     "avatarUrl": "https://cdn..."
 *   }
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    /** Capa de servicio que orquesta negocio + horarios. */
    private final BusinessService service;

    /**
     * Constructor con inyección de dependencias.
     * @param service servicio de negocios
     */
    public BusinessController(BusinessService service) {
        this.service = service;
    }

    // ---------- POST /api/businesses ----------
    /**
     * Crea un negocio y registra sus horarios de operación.
     * <p>
     * El <code>businessId</code> debe corresponder a un {@link com.mexazon.app.model.User} existente
     * con tipo "business". Si no existe, se devolverá error desde la capa de servicio.
     * </p>
     *
     * @param request mapa con <code>businessId</code>, <code>isActive</code> (opcional, por defecto true)
     *                y <code>businessHours</code> (lista de objetos con dayOfWeek, timeIn, timeOut, isWorking).
     * @return {@code 201 Created} con el {@link Business} creado.
     */
    @PostMapping
    public ResponseEntity<Business> createBusiness(@RequestBody Map<String, Object> request) {
        Long businessId = Long.valueOf(request.get("businessId").toString());
        Boolean isActive = (Boolean) request.getOrDefault("isActive", true);

        // Convertir businessHours a lista de objetos
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> hoursList = (List<Map<String, Object>>) request.get("businessHours");
        List<BusinessHour> hours = new ArrayList<>();
        for (Map<String, Object> h : hoursList) {
            BusinessHour bh = new BusinessHour();
            bh.setBusinessId(businessId);
            bh.setDayOfWeek(h.get("dayOfWeek").toString());
            bh.setTimeIn(h.get("timeIn") != null ? java.time.LocalTime.parse(h.get("timeIn").toString()) : null);
            bh.setTimeOut(h.get("timeOut") != null ? java.time.LocalTime.parse(h.get("timeOut").toString()) : null);
            bh.setWorking((Boolean) h.getOrDefault("isWorking", true));
            hours.add(bh);
        }

        Business business = new Business();
        business.setBusinessId(businessId);
        business.setActive(isActive);

        Business created = service.createBusiness(business, hours);
        return ResponseEntity.status(201).body(created);
    }

    // ---------- PATCH /api/businesses/{id} ----------
    /**
     * Actualiza información visible del negocio: nombre, descripción y avatar.
     * <p>
     * Los campos son opcionales; sólo se actualizan los presentes en el payload.
     * </p>
     *
     * @param businessId id del negocio a actualizar
     * @param updates mapa con claves opcionales: <code>name</code>, <code>description</code>, <code>avatarUrl</code>
     * @return {@code 200 OK} con el {@link Business} (estado actualizado en usuario vinculado).
     */
    @PatchMapping("/{businessId}")
    public ResponseEntity<Business> updateBusinessInfo(
            @PathVariable Long businessId,
            @RequestBody Map<String, Object> updates) {

        String name = (String) updates.get("name");
        String description = (String) updates.get("description");
        String avatarUrl = (String) updates.get("avatarUrl");

        Business updated = service.updateBusinessInfo(businessId, name, description, avatarUrl);
        return ResponseEntity.ok(updated);
    }

    // ---------- GET /api/businesses/{id} ----------
    /**
     * Obtiene el negocio con información del usuario asociado.
     *
     * @param businessId id del negocio
     * @return {@code 200 OK} con un objeto resumido, o {@code 404 Not Found} si no existe.
     */
    @GetMapping("/{businessId}")
    public ResponseEntity<Map<String, Object>> getBusiness(@PathVariable Long businessId) {
        return service.getBusinessById(businessId)
                .map(b -> {
                    Map<String, Object> resp = new LinkedHashMap<>();
                    resp.put("businessId", b.getBusinessId());
                    resp.put("isActive", b.isActive());
                    resp.put("user", Map.of(
                            "name", b.getUser().getName(),
                            "description", b.getUser().getDescription(),
                            "avatarUrl", b.getUser().getAvatarUrl()
                    ));
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- GET /api/businesses/{id}/hours ----------
    /**
     * Devuelve la lista de horarios de operación del negocio.
     *
     * @param businessId id del negocio
     * @return {@code 200 OK} con la lista de {@link BusinessHour}.
     */
    @GetMapping("/{businessId}/hours")
    public ResponseEntity<List<BusinessHour>> getBusinessHours(@PathVariable Long businessId) {
        List<BusinessHour> hours = service.getBusinessHours(businessId);
        return ResponseEntity.ok(hours);
    }
}