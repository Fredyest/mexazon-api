package com.mexazon.app.service.impl;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.User;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea (o devuelve) el negocio vinculado a un usuario de tipo "business".
     * La PK del negocio es el mismo userId.
     * - Valida que el usuario exista.
     * - Valida que sea de tipo "business".
     * - Si ya existe el negocio, lo devuelve (idempotente).
     */
    @Transactional
    public Business createFromUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!"business".equalsIgnoreCase(user.getUserType())) {
            throw new IllegalArgumentException("El usuario no es de tipo 'business'");
        }

        // Idempotencia: si ya existe, regresamos el existente
        return businessRepository.findById(userId)
            .orElseGet(() -> {
                Business b = Business.fromUser(user); // debe poner businessId = userId
                b.setActive(true);
                return businessRepository.save(b);
            });
    }

    /**
     * Activa o desactiva un negocio.
     */
    @Transactional
    public Business toggleActive(Long businessId, boolean active) {
        Business b = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
        b.setActive(active);
        return businessRepository.save(b);
    }

    /**
     * Obtiene un negocio por id (userId).
     */
    @Transactional(readOnly = true)
    public Business get(Long businessId) {
        return businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
    }

    /**
     * (Opcional) accesos de conveniencia si los necesitas:
     */
    @Transactional(readOnly = true)
    public Business getByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Business b = businessRepository.findByUser(user);
        if (b == null) throw new IllegalArgumentException("Negocio no encontrado para el usuario");
        return b;
    }
}

