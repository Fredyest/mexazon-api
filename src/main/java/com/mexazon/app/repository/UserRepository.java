package com.mexazon.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mexazon.app.model.User;

/**
 * Repositorio JPA para la entidad {@link User}.
 * <p>
 * Gestiona todas las operaciones de persistencia relacionadas con los usuarios
 * registrados en Mexazón, incluyendo autenticación, validación y mantenimiento
 * de datos generales. Además de los métodos CRUD básicos, define consultas
 * derivadas específicas para operaciones comunes como el inicio de sesión
 * y la verificación de existencia de un usuario por correo electrónico.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar.</li>
 *   <li>Utiliza {@link Optional} para manejar posibles resultados nulos al buscar por correo.</li>
 *   <li>Los métodos se basan en la convención de nombres de Spring Data JPA, evitando consultas manuales.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Buscar usuario por correo electrónico (login)
 * Optional&lt;User&gt; userOpt = userRepository.findByEmail("correo@ejemplo.com");
 *
 * // Verificar si existe un usuario con determinado correo
 * boolean exists = userRepository.existsByEmail("correo@ejemplo.com");
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code User}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     * <p>
     * Este método es comúnmente utilizado durante el proceso de autenticación.
     * </p>
     *
     * @param email correo electrónico del usuario.
     * @return un {@link Optional} con el usuario encontrado, o vacío si no existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario registrado con el correo electrónico especificado.
     *
     * @param email correo electrónico a verificar.
     * @return {@code true} si el correo ya está registrado, de lo contrario {@code false}.
     */
    boolean existsByEmail(String email);
}