package com.mexazon.app.service;

import java.util.Optional;
import com.mexazon.app.model.User;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los usuarios
 * ({@link User}) dentro de la plataforma Mexazón.
 * <p>
 * Define las operaciones esenciales para el registro, autenticación y actualización
 * de información de los usuarios, actuando como capa intermedia entre los controladores
 * REST y los repositorios de persistencia.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Separa la lógica de negocio del acceso a datos para mantener una arquitectura limpia.</li>
 *   <li>Incluye soporte para actualizaciones parciales de usuario mediante PATCH,
 *       preservando los valores no modificados.</li>
 *   <li>La validación de credenciales es manejada directamente en la capa de servicio
 *       para facilitar integración con mecanismos de autenticación futuros (JWT, OAuth, etc.).</li>
 *   <li>Utiliza {@link Optional} para manejar búsquedas seguras y evitar {@code NullPointerException}.</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Registrar nuevos usuarios (ordinarios o negocios).</li>
 *   <li>Recuperar usuarios por su identificador.</li>
 *   <li>Actualizar parcialmente información de perfil (nombre, teléfono, avatar, etc.).</li>
 *   <li>Verificar existencia de correos electrónicos registrados.</li>
 *   <li>Validar credenciales durante el inicio de sesión.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear un nuevo usuario
 * User nuevo = new User();
 * nuevo.setEmail("correo@ejemplo.com");
 * nuevo.setPassword("12345");
 * nuevo.setUserType("ordinary");
 * userService.createUser(nuevo);
 *
 * // Buscar usuario por ID
 * Optional&lt;User&gt; user = userService.getById(10L);
 *
 * // Actualizar parcialmente (ej. nombre o avatar)
 * User cambios = new User();
 * cambios.setName("Nuevo nombre");
 * cambios.setAvatarUrl("https://cdn.mexazon.com/avatar10.png");
 * userService.updatePartial(10L, cambios);
 *
 * // Validar credenciales
 * Optional&lt;User&gt; valido = userService.validateCredentials("correo@ejemplo.com", "12345");
 * </pre>
 */
public interface UserService {

    /**
     * Crea y registra un nuevo usuario en el sistema.
     *
     * @param user entidad {@link User} a registrar.
     * @return el usuario creado y persistido.
     */
    User createUser(User user);

    /**
     * Obtiene la información de un usuario por su identificador único.
     *
     * @param userId identificador del usuario.
     * @return un {@link Optional} conteniendo al usuario si existe.
     */
    Optional<User> getById(Long userId);

    /**
     * Actualiza parcialmente los datos de un usuario.
     * <p>
     * Solo se modifican los campos no nulos del objeto {@code updates}.
     * </p>
     *
     * @param userId identificador del usuario a actualizar.
     * @param updates objeto con los campos a modificar.
     * @return la entidad {@link User} actualizada.
     */
    User updatePartial(Long userId, User updates);

    /**
     * Verifica si ya existe un usuario registrado con el correo electrónico especificado.
     *
     * @param email correo electrónico a verificar.
     * @return {@code true} si el correo ya está en uso, de lo contrario {@code false}.
     */
    boolean existsByEmail(String email);

    /**
     * Valida las credenciales de inicio de sesión de un usuario.
     * <p>
     * Compara el correo y la contraseña proporcionados con los almacenados
     * en la base de datos.
     * </p>
     *
     * @param email correo electrónico del usuario.
     * @param password contraseña en texto plano (o hash, según implementación).
     * @return un {@link Optional} con el usuario si las credenciales son correctas.
     */
    Optional<User> validateCredentials(String email, String password);
}