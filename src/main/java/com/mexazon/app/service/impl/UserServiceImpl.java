package com.mexazon.app.service.impl;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.mexazon.app.model.User;
import com.mexazon.app.repository.UserRepository;
import com.mexazon.app.service.UserService;

/**
 * Implementación del servicio {@link UserService}.
 * <p>
 * Gestiona la lógica de negocio del módulo de usuarios, incluyendo
 * creación, actualización parcial, validación de credenciales
 * y consultas básicas.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Registrar nuevos usuarios en la plataforma.</li>
 *   <li>Recuperar usuarios por su identificador único.</li>
 *   <li>Actualizar parcialmente información del perfil.</li>
 *   <li>Verificar existencia de correo electrónico para evitar duplicados.</li>
 *   <li>Validar credenciales básicas de inicio de sesión (sin JWT).</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link UserRepository} como capa de persistencia basada en JPA.</li>
 *   <li>El método {@code updatePartial} permite actualizar sólo los campos enviados,
 *       manteniendo los valores previos en los no proporcionados.</li>
 *   <li>La validación de credenciales se realiza mediante comparación directa de contraseñas,
 *       adecuada únicamente para entornos de desarrollo o pruebas.</li>
 *   <li>En producción, se recomienda utilizar hashing (BCrypt, Argon2) y autenticación JWT.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear un usuario
 * User nuevo = new User();
 * nuevo.setEmail("diana@example.com");
 * nuevo.setPassword("12345");
 * nuevo.setUserType("ordinary");
 * userService.createUser(nuevo);
 *
 * // Consultar usuario por ID
 * Optional&lt;User&gt; user = userService.getById(1L);
 *
 * // Actualizar parcialmente un perfil
 * User updates = new User();
 * updates.setDescription("Amante de los tacos y las reseñas locales");
 * userService.updatePartial(1L, updates);
 *
 * // Verificar si un email ya existe
 * boolean yaExiste = userService.existsByEmail("diana@example.com");
 *
 * // Validar credenciales simples
 * Optional&lt;User&gt; login = userService.validateCredentials("diana@example.com", "12345");
 * </pre>
 */
@Service
public class UserServiceImpl implements UserService {

    /** Repositorio de usuarios. */
    private final UserRepository repository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository repositorio JPA para la entidad {@link User}.
     */
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> getById(Long userId) {
        return repository.findById(userId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Permite actualizar parcialmente los campos del perfil del usuario.
     * Solo se modifican los valores no nulos enviados en el objeto {@code updates}.
     * </p>
     *
     * @throws RuntimeException si el usuario no existe.
     */
    @Override
    public User updatePartial(Long userId, User updates) {
        return repository.findById(userId).map(existing -> {
            if (updates.getName() != null) existing.setName(updates.getName());
            if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
            if (updates.getAvatarUrl() != null) existing.setAvatarUrl(updates.getAvatarUrl());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida credenciales básicas (sin cifrado). Retorna el usuario
     * si el correo y la contraseña coinciden exactamente.
     * </p>
     *
     * @return un {@link Optional} con el usuario autenticado, o vacío si falla la validación.
     */
    @Override
    public Optional<User> validateCredentials(String email, String password) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        return Optional.empty();
    }
    
    
}