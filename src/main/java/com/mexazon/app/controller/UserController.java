package com.mexazon.app.controller;

import com.mexazon.app.model.User;
import com.mexazon.app.service.impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios ({@link User}).
 *
 * Base path: {@code /api/users}
 *
 * Endpoints:
 * <ul>
 *   <li>Crear un nuevo usuario (registro).</li>
 *   <li>Consultar usuario por su identificador.</li>
 * </ul>
 *
 * Notas:
 * - Aplica las reglas de negocio definidas en {@link UserService#createUser(User)}:
 *   <ul>
 *     <li>Campos obligatorios: email, phone, password.</li>
 *     <li>Email y teléfono deben ser únicos.</li>
 *     <li>La contraseña se almacena hasheada (BCrypt).</li>
 *     <li>Si el tipo de usuario es “business”, se genera automáticamente un registro en la tabla de negocios.</li>
 *   </ul>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------- CREATE ----------
    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Método/URL: POST /api/users
     *
     * Body:
     * <pre>
     * {
     *   "userType": "business",       // "ordinary" o "business" (obligatorio)
     *   "email": "user@email.com",    // obligatorio y único
     *   "phone": "5544332211",        // obligatorio y único
     *   "passwordHash": "mypassword", // obligatorio (se encripta automáticamente)
     *   "name": "Juan Pérez",
     *   "description": "Vendedor de comida mexicana",
     *   "avatarUrl": "https://cdn/img.png"
     * }
     * </pre>
     *
     * Responses:
     * - 201 Created: usuario creado
     * - 400 Bad Request: datos faltantes o duplicados (email/phone)
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/users" \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *         "userType": "business",
     *         "email": "juan@correo.com",
     *         "phone": "5544332211",
     *         "passwordHash": "123456",
     *         "name": "Juan Pérez"
     *       }'
     * </pre>
     *
     * @param newUser objeto {@link User} con los datos del usuario
     * @return el usuario creado, con contraseña ya hasheada
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User created = userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- READ ----------
    /**
     * Obtiene los datos de un usuario por su identificador.
     *
     * Método/URL: GET /api/users/{userId}
     *
     * Path params:
     * - {@code userId}: identificador del usuario
     *
     * Responses:
     * - 200 OK: usuario encontrado
     * - 404 Not Found: usuario no existe
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/users/5"
     * </pre>
     *
     * @param userId identificador único del usuario
     * @return el usuario correspondiente
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User found = userService.getById(userId);
        return ResponseEntity.ok(found);
    }
}