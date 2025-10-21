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
 * Controlador REST para la gestión de direcciones de usuarios ({@link UserAddress}).
 *
 * Base path: {@code /api/user-addresses}
 *
 * Endpoints principales:
 * <ul>
 *   <li>Crear dirección para un usuario.</li>
 *   <li>Consultar dirección por usuario.</li>
 *   <li>Buscar direcciones por código postal, colonia o ambos.</li>
 *   <li>Actualizar o eliminar dirección de usuario.</li>
 * </ul>
 *
 * Notas:
 * - Cada usuario puede tener solo una dirección.
 * - Las validaciones de negocio se aplican en la capa de servicio.
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

    // ---------- CREATE ----------
    /**
     * Crea una nueva dirección asociada a un usuario.
     *
     * Método/URL: POST /api/user-addresses
     *
     * Body:
     * <pre>
     * {
     *   "userId": 5,                              // ID del usuario (obligatorio)
     *   "postalCodeId": {                         // Clave compuesta del catálogo postal
     *       "postalCode": "01000",
     *       "colonia": "San Ángel"
     *   },
     *   "street": "Av. Revolución",
     *   "number": "123"
     * }
     * </pre>
     *
     * Responses:
     * - 201 Created: dirección creada
     * - 400 Bad Request: datos inválidos (campos vacíos, usuario ya tiene dirección)
     * - 404 Not Found: usuario o código postal no encontrados
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/user-addresses" \
     *   -H "Content-Type: application/json" \
     *   -d '{"userId":5,"postalCodeId":{"postalCode":"01000","colonia":"San Ángel"},"street":"Av. Revolución","number":"123"}'
     * </pre>
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

    // ---------- READ ----------
    /**
     * Obtiene la dirección registrada de un usuario por su ID.
     *
     * Método/URL: GET /api/user-addresses/{userId}
     *
     * Path params:
     * - {@code userId}: ID del usuario
     *
     * Responses:
     * - 200 OK: dirección encontrada
     * - 404 Not Found: dirección no registrada
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/user-addresses/5"
     * </pre>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserAddress> get(@PathVariable Long userId) {
        UserAddress address = userAddressService.get(userId);
        return ResponseEntity.ok(address);
    }

    /**
     * Obtiene la dirección de un usuario usando el objeto {@link User}.
     *
     * Método/URL: GET /api/user-addresses/user/{userId}
     *
     * Path params:
     * - {@code userId}: ID del usuario
     *
     * Responses:
     * - 200 OK: dirección encontrada
     * - 404 Not Found: usuario o dirección inexistente
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/user-addresses/user/5"
     * </pre>
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAddress> getByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserAddress address = userAddressService.getByUser(user);
        return ResponseEntity.ok(address);
    }

    /**
     * Lista todas las direcciones de un código postal específico.
     *
     * Método/URL: GET /api/user-addresses/postal-code/{postalCode}
     *
     * Path params:
     * - {@code postalCode}: código postal (ej. "01000")
     *
     * Responses:
     * - 200 OK: lista de direcciones
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/user-addresses/postal-code/01000"
     * </pre>
     */
    @GetMapping("/postal-code/{postalCode}")
    public ResponseEntity<List<UserAddress>> getAllByPostalCode(@PathVariable String postalCode) {
        List<UserAddress> addresses = userAddressService.getAllByPostalCode(postalCode);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Lista todas las direcciones registradas en una colonia específica.
     *
     * Método/URL: GET /api/user-addresses/colonia/{colonia}
     *
     * Path params:
     * - {@code colonia}: nombre de la colonia
     *
     * Responses:
     * - 200 OK: lista de direcciones
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/user-addresses/colonia/San%20Ángel"
     * </pre>
     */
    @GetMapping("/colonia/{colonia}")
    public ResponseEntity<List<UserAddress>> getAllByColonia(@PathVariable String colonia) {
        List<UserAddress> addresses = userAddressService.getAllByColonia(colonia);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Busca direcciones por código postal y colonia.
     *
     * Método/URL: GET /api/user-addresses/search?postalCode={postalCode}&colonia={colonia}
     *
     * Query params:
     * - {@code postalCode}: código postal (obligatorio)
     * - {@code colonia}: colonia (obligatorio)
     *
     * Responses:
     * - 200 OK: lista de direcciones encontradas
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/user-addresses/search?postalCode=01000&colonia=San%20Ángel"
     * </pre>
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserAddress>> getAllByPostalCodeAndColonia(
            @RequestParam String postalCode,
            @RequestParam String colonia) {
        List<UserAddress> addresses = userAddressService.getAllByPostalCodeAndColonia(postalCode, colonia);
        return ResponseEntity.ok(addresses);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza la dirección de un usuario.
     *
     * Método/URL: PUT /api/user-addresses/{userId}
     *
     * Path params:
     * - {@code userId}: ID del usuario
     *
     * Body (campos opcionales; null conserva el valor actual):
     * <pre>
     * {
     *   "postalCodeId": {"postalCode":"01000","colonia":"San Ángel"},
     *   "street": "Av. Insurgentes",
     *   "number": "456"
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: dirección actualizada
     * - 404 Not Found: usuario o dirección no encontrada
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT "http://localhost:8080/api/user-addresses/5" \
     *   -H "Content-Type: application/json" \
     *   -d '{"street":"Av. Insurgentes","number":"456"}'
     * </pre>
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

    // ---------- DELETE ----------
    /**
     * Elimina la dirección asociada a un usuario.
     *
     * Método/URL: DELETE /api/user-addresses/{userId}
     *
     * Path params:
     * - {@code userId}: ID del usuario
     *
     * Responses:
     * - 204 No Content: dirección eliminada
     * - 404 Not Found: dirección no encontrada
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/user-addresses/5"
     * </pre>
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userAddressService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // ========= DTOs =========

    /**
     * Payload para crear una nueva dirección de usuario.
     */
    public static class CreateUserAddressRequest {
        /** ID del usuario propietario. */
        public Long userId;
        /** Identificador compuesto del código postal. */
        public com.mexazon.app.model.PostalCodeId postalCodeId;
        /** Calle del domicilio. */
        public String street;
        /** Número del domicilio. */
        public String number;
    }

    /**
     * Payload para actualizar una dirección existente.
     */
    public static class UpdateUserAddressRequest {
        /** Identificador compuesto del nuevo código postal (opcional). */
        public com.mexazon.app.model.PostalCodeId postalCodeId;
        /** Nueva calle (opcional). */
        public String street;
        /** Nuevo número (opcional). */
        public String number;
    }
}