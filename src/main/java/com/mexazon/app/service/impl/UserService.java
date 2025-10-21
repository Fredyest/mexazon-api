package com.mexazon.app.service.impl;

import org.springframework.stereotype.Service;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.User;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Servicio para la gestión de usuarios ({@link User}).
 * <p>
 * Proporciona operaciones de registro y consulta de usuarios,
 * incluyendo validaciones de unicidad, cifrado de contraseñas
 * y creación automática de negocios para usuarios de tipo "business".
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor del servicio de usuarios.
     *
     * @param userRepository Repositorio de usuarios
     * @param businessRepository Repositorio de negocios
     * @param passwordEncoder Codificador de contraseñas (BCrypt)
     */
    public UserService(UserRepository userRepository,
                       BusinessRepository businessRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- CREATE ----------
    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Aplica las siguientes reglas de negocio:
     * <ul>
     *   <li>{@code email}, {@code phone} y {@code password} son obligatorios.</li>
     *   <li>El {@code email} y el {@code phone} deben ser únicos.</li>
     *   <li>La contraseña se guarda hasheada utilizando BCrypt.</li>
     *   <li>Si el usuario es de tipo {@code "business"}, se crea automáticamente
     *       un registro en la tabla {@code businesses} con el mismo ID.</li>
     * </ul>
     *
     * @param newUser Objeto {@link User} con los datos del nuevo usuario
     * @return El usuario creado
     * @throws IllegalArgumentException si faltan datos obligatorios o hay conflicto de unicidad
     */
    @Transactional
    public User createUser(User newUser) {
        // Validaciones básicas
        if (newUser.getEmail() == null || newUser.getEmail().isBlank())
            throw new IllegalArgumentException("email es obligatorio");
        if (newUser.getPhone() == null || newUser.getPhone().isBlank())
            throw new IllegalArgumentException("phone es obligatorio");
        if (newUser.getPasswordHash() == null || newUser.getPasswordHash().isBlank())
            throw new IllegalArgumentException("password es obligatorio");

        // Normaliza el email a minúsculas
        newUser.setEmail(newUser.getEmail().toLowerCase(Locale.ROOT));

        // Validación de unicidad previa (antes de llegar al índice de BD)
        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail()))
            throw new IllegalArgumentException("email ya registrado");
        if (userRepository.existsByPhone(newUser.getPhone()))
            throw new IllegalArgumentException("phone ya registrado");

        // Cifrado de contraseña
        newUser.setPasswordHash(passwordEncoder.encode(newUser.getPasswordHash()));

        // Guarda el usuario
        final User saved;
        try {
            saved = userRepository.save(newUser);
        } catch (DataIntegrityViolationException ex) {
            // Respaldo en caso de condición de carrera (índices únicos)
            throw new IllegalArgumentException("Conflicto de datos (email/phone duplicado)");
        }

        // Si el usuario es de tipo "business", crea su registro asociado
        if ("business".equalsIgnoreCase(saved.getUserType())) {
            Long businessId = saved.getUserId(); // la PK coincide con userId
            if (!businessRepository.existsById(businessId)) {
                Business b = Business.fromUser(saved);
                b.setActive(true);
                businessRepository.save(b);
            }
        }
        return saved;
    }

    // ---------- READ ----------
    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param userId ID del usuario a consultar
     * @return El usuario encontrado
     * @throws IllegalArgumentException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}