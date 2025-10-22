package com.mexazon.app.controller;

import com.mexazon.app.model.UserAddress;
import com.mexazon.app.service.AddressService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de direcciones de usuario.
 * <p>
 * Permite crear y consultar la dirección única asociada a cada usuario.
 * Esta información se vincula con el catálogo de códigos postales
 * para validar la existencia de colonia y alcaldía.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Registrar una dirección de usuario validando código postal y colonia.</li>
 *   <li>Recuperar la dirección completa (incluyendo alcaldía) de un usuario.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link AddressService} como capa de negocio.</li>
 *   <li>Restringe a una dirección única por usuario (verificada por <code>user_id</code>).</li>
 *   <li>Valida la existencia del código postal y colonia mediante el catálogo interno.</li>
 *   <li>Maneja errores descriptivos con códigos HTTP estándar:
 *     <ul>
 *       <li><b>201 Created</b>: dirección registrada exitosamente.</li>
 *       <li><b>404 Not Found</b>: catálogo o dirección no encontrada.</li>
 *       <li><b>409 Conflict</b>: el usuario ya tiene una dirección registrada.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>Ejemplos de uso:</h3>
 *
 * <h4>➤ Crear dirección</h4>
 * <pre>
 * PUT /api/users/7/address
 * Content-Type: application/json
 *
 * {
 *   "postalCode": "04360",
 *   "colonia": "Del Carmen",
 *   "street": "Av. Universidad",
 *   "number": "123"
 * }
 * </pre>
 * <h4>Respuesta exitosa</h4>
 * <pre>
 * 201 Created
 * {
 *   "userId": 7,
 *   "postalCode": "04360",
 *   "colonia": "Del Carmen",
 *   "alcaldia": "Coyoacán",
 *   "street": "Av. Universidad",
 *   "number": "123"
 * }
 * </pre>
 *
 * <h4>➤ Consultar dirección</h4>
 * <pre>
 * GET /api/users/7/address
 * </pre>
 * <h4>Respuesta exitosa</h4>
 * <pre>
 * 200 OK
 * {
 *   "userId": 7,
 *   "postalCode": "04360",
 *   "colonia": "Del Carmen",
 *   "alcaldia": "Coyoacán",
 *   "street": "Av. Universidad",
 *   "number": "123"
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/users/{userId}/address")
public class UserAddressController {

    /** Servicio de negocio encargado de la gestión de direcciones. */
    private final AddressService addressService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param addressService servicio para manejo de direcciones y catálogo postal.
     */
    public UserAddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // ---------- PUT /api/users/{userId}/address ----------
    /**
     * Crea una nueva dirección para un usuario, validando que:
     * <ul>
     *   <li>No exista ya una dirección registrada para el mismo usuario.</li>
     *   <li>El código postal y colonia existan en el catálogo.</li>
     * </ul>
     *
     * @param userId identificador del usuario propietario.
     * @param address entidad {@link UserAddress} con la dirección enviada.
     * @return {@code 201 Created} con la dirección creada,
     *         {@code 404 Not Found} si el catálogo no la reconoce,
     *         o {@code 409 Conflict} si ya existe una dirección.
     */
    @PutMapping
    public ResponseEntity<?> createAddress(@PathVariable Long userId, @RequestBody UserAddress address) {
        try {
            address.setUserId(userId);
            var saved = addressService.createAddress(address);
            var catalog = addressService.getCatalogEntry(saved.getPostalCode(), saved.getColonia());
            var response = Map.of(
                    "userId", saved.getUserId(),
                    "postalCode", saved.getPostalCode(),
                    "colonia", saved.getColonia(),
                    "alcaldia", catalog.get("alcaldia"),
                    "street", saved.getStreet(),
                    "number", saved.getNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // ---------- GET /api/users/{userId}/address ----------
    /**
     * Obtiene la dirección actual registrada de un usuario.
     *
     * @param userId identificador del usuario.
     * @return {@code 200 OK} con la dirección encontrada o {@code 404 Not Found} si no existe.
     */
    @GetMapping
    public ResponseEntity<?> getAddress(@PathVariable Long userId) {
        var opt = addressService.getUserAddress(userId);
        if (opt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("message", "Address not found"));

        var a = opt.get();
        var catalog = addressService.getCatalogEntry(a.getPostalCode(), a.getColonia());
        return ResponseEntity.ok(Map.of(
                "userId", a.getUserId(),
                "postalCode", a.getPostalCode(),
                "colonia", a.getColonia(),
                "alcaldia", catalog.get("alcaldia"),
                "street", a.getStreet(),
                "number", a.getNumber()
        ));
    }
}