package com.mexazon.app.controller;

import com.mexazon.app.model.User;
import com.mexazon.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios.
 * <p>
 * Permite crear nuevos usuarios, consultar información existente,
 * realizar actualizaciones parciales y verificar la disponibilidad de correos electrónicos.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Registrar nuevos usuarios.</li>
 *   <li>Obtener datos de usuario por su identificador.</li>
 *   <li>Actualizar parcialmente información del perfil.</li>
 *   <li>Verificar si un correo electrónico ya está registrado (para evitar duplicados).</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link UserService} para la lógica de negocio y persistencia.</li>
 *   <li>Las contraseñas se guardan en texto plano en esta versión (no cifradas),
 *       ya que la autenticación se gestiona sin JWT.</li>
 *   <li>En futuras versiones se recomienda integrar hashing (BCrypt) y autenticación segura.</li>
 * </ul>
 *
 * <h3>Códigos de estado HTTP:</h3>
 * <ul>
 *   <li><b>201 Created</b>: usuario creado correctamente.</li>
 *   <li><b>200 OK</b>: consulta o actualización exitosa.</li>
 *   <li><b>404 Not Found</b>: usuario no encontrado.</li>
 * </ul>
 *
 * <h3>Ejemplos de uso:</h3>
 *
 * <h4>➤ Crear usuario</h4>
 * <pre>
 * POST /api/users
 * Content-Type: application/json
 * {
 *   "userType": "ordinary",
 *   "email": "ana@example.com",
 *   "password": "12345",
 *   "phone": "5544332211",
 *   "name": "Ana López"
 * }
 * </pre>
 * <h4>Respuesta</h4>
 * <pre>
 * 201 Created
 * {
 *   "userId": 7,
 *   "email": "ana@example.com",
 *   "userType": "ordinary",
 *   "createdAt": "2025-10-21T21:15:00"
 * }
 * </pre>
 *
 * <h4>➤ Verificar email</h4>
 * <pre>
 * GET /api/users/exists?email=ana@example.com
 * </pre>
 * <h4>Respuesta</h4>
 * <pre>
 * { "exists": true }
 * </pre>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** Servicio de negocio encargado de gestionar los usuarios. */
    private final UserService service;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param service servicio de usuarios
     */
    public UserController(UserService service) {
        this.service = service;
    }

    // ---------- POST /api/users ----------
    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param newUser entidad {@link User} con los datos de registro.
     * @return {@code 201 Created} con el usuario creado.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User created = service.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- GET /api/users/{userId} ----------
    /**
     * Obtiene los datos de un usuario por su identificador.
     *
     * @param userId ID del usuario a consultar.
     * @return {@code 200 OK} con el usuario o {@code 404 Not Found} si no existe.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        return service.getById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ---------- PATCH /api/users/{userId} ----------
    /**
     * Actualiza parcialmente los datos de un usuario.
     * <p>
     * Solo se actualizan los campos enviados en la petición:
     * <ul>
     *   <li><code>name</code></li>
     *   <li><code>description</code></li>
     *   <li><code>avatarUrl</code></li>
     * </ul>
     * </p>
     *
     * @param userId ID del usuario.
     * @param updates objeto {@link User} con los campos a modificar.
     * @return {@code 200 OK} si se actualizó, o {@code 404 Not Found} si el usuario no existe.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<User> updatePartial(@PathVariable("userId") Long userId, @RequestBody User updates) {
        try {
            User updated = service.updatePartial(userId, updates);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ---------- GET /api/users/exists ----------
    /**
     * Verifica si un correo electrónico ya está registrado en el sistema.
     *
     * @param email correo electrónico a verificar.
     * @return {@code 200 OK} con <code>{"exists": true|false}</code>.
     */
    @GetMapping("/exists")
    public ResponseEntity<?> checkEmailExists(@RequestParam("email") String email) {
        boolean exists = service.existsByEmail(email);
        return ResponseEntity.ok().body(java.util.Map.of("exists", exists));
    }
}