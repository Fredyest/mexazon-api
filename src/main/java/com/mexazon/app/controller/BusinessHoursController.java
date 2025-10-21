package com.mexazon.app.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.BusinessHours;
import com.mexazon.app.model.WeekDay;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.service.impl.BusinessHoursService;

/**
 * Controlador REST para gestionar los horarios de negocio ({@link BusinessHours}).
 *
 * Base path: {@code /api/business-hours}
 * Entidad principal: Horario por día para un {@link Business}.
 *
 * Autenticación/Autorización: (definir si aplica, p. ej.: requiere JWT; roles: OWNER/ADMIN)
 * Notas:
 * - Los días se manejan con {@link WeekDay}.
 * - La combinación (businessId + day) es única por semana.
 */
@RestController
@RequestMapping("/api/business-hours")
public class BusinessHoursController {

    private final BusinessHoursService businessHoursService;
    private final BusinessRepository businessRepository;

    public BusinessHoursController(BusinessHoursService businessHoursService,
                                   BusinessRepository businessRepository) {
        this.businessHoursService = businessHoursService;
        this.businessRepository = businessRepository;
    }

    /**
     * Crea un nuevo horario para un negocio.
     *
     * Método/URL: POST /api/business-hours
     * Body:
     * <pre>
     * {
     *   "businessId": 123,
     *   "day": "MONDAY",        // Enum WeekDay
     *   "timeIn": "09:00:00",   // HH:mm:ss
     *   "timeOut": "18:00:00",  // HH:mm:ss
     *   "isWorking": true
     * }
     * </pre>
     *
     * Responses:
     * - 201 Created: horario creado
     * - 400 Bad Request: validaciones (p. ej., {@code timeOut} <= {@code timeIn}, negocio/día duplicado)
     * - 404 Not Found: negocio inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X POST http://localhost:8080/api/business-hours \
     *   -H "Content-Type: application/json" \
     *   -d '{"businessId":1,"day":"MONDAY","timeIn":"09:00:00","timeOut":"18:00:00","isWorking":true}'
     * </pre>
     */
    @PostMapping
    public ResponseEntity<BusinessHours> create(@RequestBody CreateBusinessHoursRequest request) {
        Business business = businessRepository.findById(request.businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));

        BusinessHours created = businessHoursService.create(
            business,
            request.day,
            request.timeIn,
            request.timeOut,
            request.isWorking
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene el horario de un negocio para un día específico.
     *
     * Método/URL: GET /api/business-hours/{businessId}/{dayOfWeek}
     * Path params:
     * - businessId: ID del negocio
     * - dayOfWeek: índice del día (0-6; correspondencia con {@link WeekDay#values()})
     *
     * Responses:
     * - 200 OK: retorna el horario
     * - 404 Not Found: horario no encontrado
     *
     * Ejemplo:
     * <pre>
     * curl http://localhost:8080/api/business-hours/1/0
     * </pre>
     */
    @GetMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<BusinessHours> get(@PathVariable Long businessId,
                                             @PathVariable int dayOfWeek) {
        BusinessHours hours = businessHoursService.get(businessId, dayOfWeek);
        return ResponseEntity.ok(hours);
    }

    /**
     * Obtiene todos los horarios de un negocio.
     *
     * Método/URL: GET /api/business-hours/business/{businessId}
     * Path params:
     * - businessId: ID del negocio
     *
     * Responses:
     * - 200 OK: lista de horarios (7 elementos o los existentes)
     * - 404 Not Found: negocio inexistente
     *
     * Ejemplo:
     * <pre>
     * curl http://localhost:8080/api/business-hours/business/1
     * </pre>
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessHours>> getAllByBusiness(@PathVariable Long businessId) {
        Business business = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));

        List<BusinessHours> hours = businessHoursService.getAllByBusiness(business);
        return ResponseEntity.ok(hours);
    }

    /**
     * Actualiza el horario de un negocio para un día específico.
     *
     * Método/URL: PUT /api/business-hours/{businessId}/{dayOfWeek}
     * Path params:
     * - businessId: ID del negocio
     * - dayOfWeek: índice del día (0-6)
     *
     * Body (todos opcionales; null = mantiene valor actual):
     * <pre>
     * {
     *   "timeIn": "10:00:00",
     *   "timeOut": "19:00:00",
     *   "isWorking": true
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: horario actualizado
     * - 400 Bad Request: validación {@code timeOut} > {@code timeIn}
     * - 404 Not Found: horario no encontrado
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT http://localhost:8080/api/business-hours/1/0 \
     *   -H "Content-Type: application/json" \
     *   -d '{"timeIn":"10:00:00","timeOut":"19:00:00","isWorking":true}'
     * </pre>
     */
    @PutMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<BusinessHours> update(@PathVariable Long businessId,
                                                @PathVariable int dayOfWeek,
                                                @RequestBody UpdateBusinessHoursRequest request) {
        BusinessHours updated = businessHoursService.update(
            businessId,
            dayOfWeek,
            request.timeIn,
            request.timeOut,
            request.isWorking
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * Marca un día como cerrado para un negocio.
     *
     * Método/URL: PUT /api/business-hours/{businessId}/{dayOfWeek}/close
     * Path params:
     * - businessId: ID del negocio
     * - dayOfWeek: índice del día (0-6)
     *
     * Responses:
     * - 200 OK: horario actualizado (isWorking=false, horas en null)
     * - 404 Not Found: horario no encontrado
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT http://localhost:8080/api/business-hours/1/0/close
     * </pre>
     */
    @PutMapping("/{businessId}/{dayOfWeek}/close")
    public ResponseEntity<BusinessHours> close(@PathVariable Long businessId,
                                               @PathVariable int dayOfWeek) {
        BusinessHours closed = businessHoursService.close(businessId, dayOfWeek);
        return ResponseEntity.ok(closed);
    }

    /**
     * Elimina el horario de un negocio para un día específico.
     *
     * Método/URL: DELETE /api/business-hours/{businessId}/{dayOfWeek}
     * Path params:
     * - businessId: ID del negocio
     * - dayOfWeek: índice del día (0-6)
     *
     * Responses:
     * - 204 No Content: eliminado con éxito
     * - 404 Not Found: horario no encontrado
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE http://localhost:8080/api/business-hours/1/0
     * </pre>
     */
    @DeleteMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<Void> delete(@PathVariable Long businessId,
                                       @PathVariable int dayOfWeek) {
        businessHoursService.delete(businessId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }

    // ========= DTOs de request =========

    /**
     * Payload para crear un horario.
     */
    public static class CreateBusinessHoursRequest {
        /** ID del negocio propietario. */
        public Long businessId;
        /** Día de la semana (enum). */
        public WeekDay day;
        /** Hora de apertura (HH:mm:ss). */
        public LocalTime timeIn;
        /** Hora de cierre (HH:mm:ss). */
        public LocalTime timeOut;
        /** Indica si el negocio está abierto ese día. */
        public boolean isWorking;
    }

    /**
     * Payload para actualizar un horario (parcial).
     * Campos en null conservan su valor previo.
     */
    public static class UpdateBusinessHoursRequest {
        public LocalTime timeIn;
        public LocalTime timeOut;
        public Boolean isWorking;
    }
}