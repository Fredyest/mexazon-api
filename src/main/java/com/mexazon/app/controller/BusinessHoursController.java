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
 * Controlador REST para gestionar los horarios de negocio.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar horarios.
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
     * POST /api/business-hours
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
     * GET /api/business-hours/{businessId}/{dayOfWeek}
     */
    @GetMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<BusinessHours> get(@PathVariable Long businessId,
                                             @PathVariable int dayOfWeek) {
        BusinessHours hours = businessHoursService.get(businessId, dayOfWeek);
        return ResponseEntity.ok(hours);
    }

    /**
     * Obtiene todos los horarios de un negocio.
     * GET /api/business-hours/business/{businessId}
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
     * PUT /api/business-hours/{businessId}/{dayOfWeek}
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
     * PUT /api/business-hours/{businessId}/{dayOfWeek}/close
     */
    @PutMapping("/{businessId}/{dayOfWeek}/close")
    public ResponseEntity<BusinessHours> close(@PathVariable Long businessId,
                                               @PathVariable int dayOfWeek) {
        BusinessHours closed = businessHoursService.close(businessId, dayOfWeek);
        return ResponseEntity.ok(closed);
    }

    /**
     * Elimina el horario de un negocio para un día específico.
     * DELETE /api/business-hours/{businessId}/{dayOfWeek}
     */
    @DeleteMapping("/{businessId}/{dayOfWeek}")
    public ResponseEntity<Void> delete(@PathVariable Long businessId,
                                       @PathVariable int dayOfWeek) {
        businessHoursService.delete(businessId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clase interna para la solicitud de creación de horario.
     */
    public static class CreateBusinessHoursRequest {
        public Long businessId;
        public WeekDay day;
        public LocalTime timeIn;
        public LocalTime timeOut;
        public boolean isWorking;
    }

    /**
     * Clase interna para la solicitud de actualización de horario.
     */
    public static class UpdateBusinessHoursRequest {
        public LocalTime timeIn;
        public LocalTime timeOut;
        public Boolean isWorking;
    }
}
