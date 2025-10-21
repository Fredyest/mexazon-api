package com.mexazon.app.service.impl;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.User;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de entidades {@link Business}.
 * 
 * Proporciona operaciones relacionadas con la creación, activación/desactivación
 * y obtención de negocios vinculados a usuarios de tipo "business".
 * 
 * La clave primaria del negocio coincide con el {@code userId} del usuario propietario,
 * asegurando una relación 1:1 entre {@link User} y {@link Business}.
 */
@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    /**
     * Constructor del servicio de negocios.
     *
     * @param businessRepository Repositorio de negocios
     * @param userRepository Repositorio de usuarios
     */
    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    // ---------- CREATE ----------
    /**
     * Crea (o devuelve) el negocio asociado a un usuario de tipo "business".
     * <p>
     * - Valida que el usuario exista. <br>
     * - Verifica que su tipo sea "business". <br>
     * - Si ya existe un negocio vinculado, lo devuelve (operación idempotente). <br>
     * - Si no existe, crea uno nuevo con el mismo {@code userId} como {@code businessId}.
     * 
     * @param userId ID del usuario que será propietario del negocio
     * @return El negocio existente o el nuevo creado
     * @throws IllegalArgumentException si el usuario no existe o no es de tipo "business"
     */
    @Transactional
    public Business createFromUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!"business".equalsIgnoreCase(user.getUserType())) {
            throw new IllegalArgumentException("El usuario no es de tipo 'business'");
        }

        // Idempotencia: si ya existe un negocio asociado, se devuelve el mismo
        return businessRepository.findById(userId)
            .orElseGet(() -> {
                Business b = Business.fromUser(user); // Asigna businessId = userId
                b.setActive(true);
                return businessRepository.save(b);
            });
    }

    // ---------- UPDATE ----------
    /**
     * Activa o desactiva un negocio existente.
     * <p>
     * Permite cambiar el estado de disponibilidad de un negocio sin eliminarlo.
     *
     * @param businessId ID del negocio
     * @param active Nuevo estado (true = activo, false = inactivo)
     * @return El negocio actualizado con el nuevo estado
     * @throws IllegalArgumentException si el negocio no existe
     */
    @Transactional
    public Business toggleActive(Long businessId, boolean active) {
        Business b = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
        b.setActive(active);
        return businessRepository.save(b);
    }

    // ---------- READ ----------
    /**
     * Obtiene un negocio por su identificador.
     *
     * @param businessId ID del negocio (equivalente al userId del propietario)
     * @return El negocio encontrado
     * @throws IllegalArgumentException si no se encuentra el negocio
     */
    @Transactional(readOnly = true)
    public Business get(Long businessId) {
        return businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
    }

    /**
     * Obtiene un negocio asociado a un usuario específico.
     * <p>
     * Este método valida primero la existencia del usuario y luego busca el negocio
     * que tenga relación directa con él.
     *
     * @param userId ID del usuario propietario del negocio
     * @return El negocio vinculado al usuario
     * @throws IllegalArgumentException si el usuario o el negocio no existen
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