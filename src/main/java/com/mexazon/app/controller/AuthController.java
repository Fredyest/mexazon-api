package com.mexazon.app.controller;

import com.mexazon.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Controlador REST encargado de la autenticación básica de usuarios.
 * <p>
 * Expone el endpoint <b>/api/auth/login</b> para validar credenciales
 * mediante correo electrónico y contraseña.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Recibir las credenciales enviadas por el cliente.</li>
 *   <li>Delegar la validación al {@link UserService}.</li>
 *   <li>Responder con un objeto JSON que indica si el inicio de sesión fue válido.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>No implementa generación de tokens (JWT u otros), solo validación directa.</li>
 *   <li>Devuelve una respuesta compacta con las claves:
 *     <ul>
 *       <li>{@code valid}: indica si las credenciales fueron correctas.</li>
 *       <li>{@code userId}: identificador del usuario autenticado (si aplica).</li>
 *       <li>{@code userType}: tipo de usuario ("ordinary" o "business").</li>
 *     </ul>
 *   </li>
 *   <li>En caso de credenciales incorrectas, solo retorna <code>{"valid": false}</code>.</li>
 *   <li>Este enfoque resulta útil en entornos de desarrollo o pruebas ligeras sin seguridad JWT.</li>
 * </ul>
 *
 * <h3>Ejemplo de solicitud:</h3>
 * <pre>
 * POST /api/auth/login
 * Content-Type: application/json
 *
 * {
 *   "email": "juan@example.com",
 *   "password": "12345"
 * }
 * </pre>
 *
 * <h3>Ejemplo de respuesta exitosa:</h3>
 * <pre>
 * {
 *   "valid": true,
 *   "userId": 7,
 *   "userType": "business"
 * }
 * </pre>
 *
 * <h3>Ejemplo de respuesta inválida:</h3>
 * <pre>
 * {
 *   "valid": false
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Servicio encargado de la validación de credenciales de usuario. */
    private final UserService userService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param userService servicio de usuarios para validar credenciales.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ---------- POST /api/auth/login ----------

    /**
     * Valida las credenciales del usuario (email y contraseña).
     * <p>
     * No genera tokens; únicamente confirma si las credenciales son válidas.
     * </p>
     *
     * @param loginRequest mapa JSON con claves {@code email} y {@code password}.
     * @return respuesta con los datos del usuario si es válido,
     *         o <code>{"valid": false}</code> si falla la autenticación.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        return userService.validateCredentials(email, password)
                .map(user -> ResponseEntity.ok(
                        Map.<String, Object>of(
                                "valid", true,
                                "userId", user.getUserId(),
                                "userType", user.getUserType()
                        )
                ))
                .orElse(ResponseEntity.ok(Map.<String, Object>of("valid", false)));
    }
}