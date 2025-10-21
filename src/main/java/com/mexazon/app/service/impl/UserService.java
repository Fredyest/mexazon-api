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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BusinessRepository businessRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un usuario. Reglas:
     * - email, phone, password obligatorios
     * - email/phone únicos
     * - password se guarda hasheado (BCrypt)
     * - si userType == "business" → crea registro en businesses (idempotente)
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

        // Normaliza email
        newUser.setEmail(newUser.getEmail().toLowerCase(Locale.ROOT));

        // Unicidad previa (evita error feo de BD)
        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail()))
            throw new IllegalArgumentException("email ya registrado");
        if (userRepository.existsByPhone(newUser.getPhone()))
            throw new IllegalArgumentException("phone ya registrado");

        // Hash de contraseña
        newUser.setPasswordHash(passwordEncoder.encode(newUser.getPasswordHash()));

        // Guarda usuario
        final User saved;
        try {
            saved = userRepository.save(newUser);
        } catch (DataIntegrityViolationException ex) {
            // respaldo por índices únicos (condición de carrera)
            throw new IllegalArgumentException("Conflicto de datos (email/phone duplicado)");
        }

        // Si es business, crea su fila en businesses (PK = user_id)
        if ("business".equalsIgnoreCase(saved.getUserType())) {
        Long businessId = saved.getUserId(); // usamos Long directamente
            if (!businessRepository.existsById(businessId)) {
                Business b = Business.fromUser(saved);
                b.setActive(true);
                businessRepository.save(b);
            }
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
