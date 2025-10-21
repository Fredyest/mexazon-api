package com.mexazon.app.controller;

import com.mexazon.app.model.Business;
import com.mexazon.app.service.impl.BusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    private final BusinessService service;

    public BusinessController(BusinessService service) {
        this.service = service;
    }

    // Crea el negocio ligado a un userId (o devuelve el existente si ya estaba creado)
    @PostMapping
    public ResponseEntity<Business> create(@RequestParam("userId") Long userId) {
        Business created = service.createFromUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Activa / desactiva negocio
    @PatchMapping("/{id}/active")
    public Business setActive(@PathVariable("id") Long businessId, @RequestBody ActiveDto body) {
        boolean active = (body != null && body.active != null) ? body.active : true;
        return service.toggleActive(businessId, active);
    }

    // Obtiene negocio por id (== userId)
    @GetMapping("/{id}")
    public Business get(@PathVariable("id") Long businessId) {
        return service.get(businessId);
    }

    // DTO mínimo para PATCH
    public static class ActiveDto {
        public Boolean active;
    }
}
