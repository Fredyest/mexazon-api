package com.mexazon.app.controller;

import com.mexazon.app.model.BusinessHour;
import com.mexazon.app.model.BusinessHourId;
import com.mexazon.app.service.BusinessHourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los horarios de operación de los negocios
 * ({@link BusinessHour}).
 * <p>
 * Aunque los horarios suelen administrarse indirectamente desde
 * {@link com.mexazon.app.controller.BusinessController}, este controlador
 * ofrece endpoints específicos para operaciones CRUD directas, 
 * útiles en integraciones o paneles administrativos.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Listar todos los horarios asociados a un negocio.</li>
 *   <li>Consultar un horario específico por día.</li>
 *   <li>Crear o actualizar horarios individuales o en bloque.</li>
 *   <li>Eliminar horarios específicos o todos los de un negocio.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Usa el servicio {@link BusinessHourService} para encapsular la lógica de negocio.</li>
 *   <li>Retorna respuestas semánticamente correctas:
 *       <ul>
 *         <li><strong>200 OK</strong> → lecturas y actualizaciones exitosas.</li>
 *         <li><strong>201 Created</strong> → creación de horarios.</li>
 *         <li><strong>204 No Content</strong> → eliminaciones.</li>
 *         <li><strong>404 Not Found</strong> → horario inexistente.</li>
 *       </ul>
 *   </li>
 *   <li>Utiliza {@link BusinessHourId} como llave compuesta para identificar horarios únicos.</li>
 * </ul>
 *
 * <h3>Ejemplos de uso</h3>
 *
 * <h4>➤ Crear horario individual</h4>
 * <pre>
 * POST /api/business-hours
 * Content-Type: application/json
 *
 * {
 *   "businessId": 5,
 *   "dayOfWeek": "Mon",
 *   "timeIn": "09:00",
 *   "timeOut": "18:00",
 *   "isWorking": true
 * }
 * </pre>
 *
 * <h4>➤ Crear varios horarios</h4>
 * <pre>
 * POST /api/business-hours/bulk
 * [
 *   {"businessId":5,"dayOfWeek":"Mon","timeIn":"09:00","timeOut":"18:00"},
 *   {"businessId":5,"dayOfWeek":"Tue","timeIn":"09:00","timeOut":"18:00"}
 * ]
 * </pre>
 *
 * <h4>➤ Eliminar un horario</h4>
 * <pre>
 * DELETE /api/business-hours/5/Mon
 * </pre>
 */
@RestController
@RequestMapping("/api/business-hours")
public class BusinessHourController {

    /** Servicio encargado de la lógica de negocio de horarios. */
    private final BusinessHourService service;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param service servicio de gestión de horarios
     */
    public BusinessHourController(BusinessHourService service) {
        this.service = service;
    }

    // ---------- GET /api/business-hours/{businessId} ----------
    /**
     * Obtiene todos los horarios registrados para un negocio.
     *
     * @param businessId identificador del negocio
     * @return lista de {@link BusinessHour} con código 200 OK
     */
    @GetMapping("/{businessId}")
    public ResponseEntity<List<BusinessHour>> getHoursByBusiness(@PathVariable Long businessId) {
        List<BusinessHour> hours = service.getByBusinessId(businessId);
        return ResponseEntity.ok(hours);
    }

    // ---------- GET /api/business-hours/{businessId}/{dayOfWeek} ----------
    /**
     * Recupera el horario de un negocio para un día específico.
     *
     * @param businessId id del negocio
     * @param dayOfWeek día de la semana (ej. "Mon", "Tue", ...)
     * @return horario encontrado o 404 si no existe
     */
    @GetMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<BusinessHour> getHourByDay(
            @PathVariable Long businessId,
            @PathVariable String dayOfWeek) {

        BusinessHourId id = new BusinessHourId(businessId, dayOfWeek);
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- POST /api/business-hours ----------
    /**
     * Crea o actualiza un horario individual.
     * Si el horario ya existe (por día y negocio), se actualiza.
     *
     * @param businessHour entidad {@link BusinessHour} a guardar
     * @return horario persistido con código 201 Created
     */
    @PostMapping
    public ResponseEntity<BusinessHour> saveHour(@RequestBody BusinessHour businessHour) {
        BusinessHour saved = service.save(businessHour);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- POST /api/business-hours/bulk ----------
    /**
     * Crea o actualiza varios horarios de un negocio en una sola operación.
     *
     * @param businessHours lista de horarios a guardar
     * @return lista persistida con código 201 Created
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<BusinessHour>> saveAll(@RequestBody List<BusinessHour> businessHours) {
        List<BusinessHour> saved = service.saveAll(businessHours);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- DELETE /api/business-hours/{businessId}/{dayOfWeek} ----------
    /**
     * Elimina un horario específico de un negocio por su día.
     *
     * @param businessId id del negocio
     * @param dayOfWeek día a eliminar
     * @return respuesta vacía con código 204 No Content
     */
    @DeleteMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<Void> deleteHour(
            @PathVariable Long businessId,
            @PathVariable String dayOfWeek) {

        BusinessHourId id = new BusinessHourId(businessId, dayOfWeek);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- DELETE /api/business-hours/{businessId} ----------
    /**
     * Elimina todos los horarios asociados a un negocio.
     *
     * @param businessId id del negocio
     * @return respuesta vacía con código 204 No Content
     */
    @DeleteMapping("/{businessId}")
    public ResponseEntity<Void> deleteAll(@PathVariable Long businessId) {
        service.deleteByBusinessId(businessId);
        return ResponseEntity.noContent().build();
    }
}