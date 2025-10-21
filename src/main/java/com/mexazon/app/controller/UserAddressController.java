package com.mexazon.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.User;
import com.mexazon.app.model.UserAddress;
import com.mexazon.app.repository.PostalCodeCatalogRepository;
import com.mexazon.app.repository.UserRepository;
import com.mexazon.app.service.impl.UserAddressService;

/**
 * Controlador REST para gestionar las direcciones de los usuarios.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar direcciones.
 */
@RestController
@RequestMapping("/api/user-addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;
    private final UserRepository userRepository;
    private final PostalCodeCatalogRepository postalCodeRepository;

    public UserAddressController(UserAddressService userAddressService,
                                 UserRepository userRepository,
                                 PostalCodeCatalogRepository postalCodeRepository) {
        this.userAddressService = userAddressService;
        this.userRepository = userRepository;
        this.postalCodeRepository = postalCodeRepository;
    }

    /**
     * Crea una nueva dirección para un usuario.
     * POST /api/user-addresses
     */
    @PostMapping
    public ResponseEntity<UserAddress> create(@RequestBody CreateUserAddressRequest request) {
        User user = userRepository.findById(request.userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        PostalCodeCatalog postalCode = postalCodeRepository.findById(request.postalCodeId)
            .orElseThrow(() -> new IllegalArgumentException("Código postal no encontrado"));

        UserAddress created = userAddressService.create(
            user,
            postalCode,
            request.street,
            request.number
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene la dirección de un usuario por su ID.
     * GET /api/user-addresses/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserAddress> get(@PathVariable Long userId) {
        UserAddress address = userAddressService.get(userId);
        return ResponseEntity.ok(address);
    }

    /**
     * Obtiene la dirección de un usuario por el objeto User.
     * GET /api/user-addresses/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAddress> getByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserAddress address = userAddressService.getByUser(user);
        return ResponseEntity.ok(address);
    }

    /**
     * Obtiene todas las direcciones de un código postal específico.
     * GET /api/user-addresses/postal-code/{postalCode}
     */
    @GetMapping("/postal-code/{postalCode}")
    public ResponseEntity<List<UserAddress>> getAllByPostalCode(@PathVariable String postalCode) {
        List<UserAddress> addresses = userAddressService.getAllByPostalCode(postalCode);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Obtiene todas las direcciones de una colonia específica.
     * GET /api/user-addresses/colonia/{colonia}
     */
    @GetMapping("/colonia/{colonia}")
    public ResponseEntity<List<UserAddress>> getAllByColonia(@PathVariable String colonia) {
        List<UserAddress> addresses = userAddressService.getAllByColonia(colonia);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Obtiene todas las direcciones por código postal y colonia.
     * GET /api/user-addresses/search?postalCode={postalCode}&colonia={colonia}
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserAddress>> getAllByPostalCodeAndColonia(
            @RequestParam String postalCode,
            @RequestParam String colonia) {
        List<UserAddress> addresses = userAddressService.getAllByPostalCodeAndColonia(postalCode, colonia);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Actualiza la dirección de un usuario.
     * PUT /api/user-addresses/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserAddress> update(@PathVariable Long userId,
                                              @RequestBody UpdateUserAddressRequest request) {
        PostalCodeCatalog postalCode = null;
        if (request.postalCodeId != null) {
            postalCode = postalCodeRepository.findById(request.postalCodeId)
                .orElseThrow(() -> new IllegalArgumentException("Código postal no encontrado"));
        }

        UserAddress updated = userAddressService.update(
            userId,
            postalCode,
            request.street,
            request.number
        );

        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina la dirección de un usuario.
     * DELETE /api/user-addresses/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userAddressService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clase interna para la solicitud de creación de dirección.
     */
    public static class CreateUserAddressRequest {
        public Long userId;
        public com.mexazon.app.model.PostalCodeId postalCodeId;
        public String street;
        public String number;
    }

    /**
     * Clase interna para la solicitud de actualización de dirección.
     */
    public static class UpdateUserAddressRequest {
        public com.mexazon.app.model.PostalCodeId postalCodeId;
        public String street;
        public String number;
    }
}