package com.mexazon.app.service.impl;

import com.mexazon.app.model.*;
import com.mexazon.app.repository.*;
import com.mexazon.app.service.BusinessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio {@link BusinessService}.
 * <p>
 * Gestiona la lógica de negocio relacionada con los negocios registrados en Mexazón,
 * incluyendo su creación, actualización y la administración de horarios de operación.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Registrar un negocio asociado a un usuario existente.</li>
 *   <li>Guardar los horarios de operación de un negocio durante su creación.</li>
 *   <li>Actualizar información general del negocio (nombre, descripción, avatar).</li>
 *   <li>Recuperar negocios y sus horarios desde la base de datos.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Usa tres repositorios:
 *     <ul>
 *       <li>{@link BusinessRepository} para los datos principales del negocio.</li>
 *       <li>{@link BusinessHourRepository} para la persistencia de horarios.</li>
 *       <li>{@link UserRepository} para validar y actualizar la información del usuario vinculado.</li>
 *     </ul>
 *   </li>
 *   <li>El identificador del negocio ({@code businessId}) coincide con el ID del usuario asociado,
 *       ya que existe una relación uno a uno entre {@link Business} y {@link User}.</li>
 *   <li>Los métodos lanzan {@link RuntimeException} ante casos de datos inexistentes,
 *       aunque en una implementación más robusta podrían emplearse excepciones personalizadas.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear un nuevo negocio con sus horarios
 * Business b = new Business();
 * b.setBusinessId(10L);
 * List&lt;BusinessHour&gt; horarios = defaultHours();
 * businessService.createBusiness(b, horarios);
 *
 * // Actualizar la información visible del negocio
 * businessService.updateBusinessInfo(10L, "Taquería Los Güeros", "Especialidad en tacos al pastor", null);
 *
 * // Consultar un negocio
 * Optional&lt;Business&gt; negocio = businessService.getBusinessById(10L);
 *
 * // Obtener sus horarios
 * List&lt;BusinessHour&gt; hours = businessService.getBusinessHours(10L);
 * </pre>
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    /** Repositorio de datos del negocio. */
    private final BusinessRepository businessRepository;

    /** Repositorio de horarios de negocio. */
    private final BusinessHourRepository hourRepository;

    /** Repositorio de usuarios. */
    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param businessRepository repositorio de negocios.
     * @param hourRepository repositorio de horarios.
     * @param userRepository repositorio de usuarios.
     */
    public BusinessServiceImpl(BusinessRepository businessRepository,
                               BusinessHourRepository hourRepository,
                               UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.hourRepository = hourRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea un nuevo negocio asociado a un usuario existente
     * y registra sus horarios de operación.
     *
     * @param business entidad {@link Business} a crear.
     * @param hours lista de horarios del negocio.
     * @return negocio creado y persistido.
     * @throws RuntimeException si el usuario no existe en la base de datos.
     */
    @Override
    public Business createBusiness(Business business, List<BusinessHour> hours) {
        // Validar que el usuario exista
        Long businessId = business.getBusinessId();
        User user = userRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        business.setUser(user);
        Business savedBusiness = businessRepository.save(business);

        // Guardar horarios asociados
        for (BusinessHour bh : hours) {
            bh.setBusinessId(businessId);
            hourRepository.save(bh);
        }

        return savedBusiness;
    }

    /**
     * Actualiza la información general del negocio (nombre, descripción, avatar).
     * <p>
     * Los cambios se reflejan directamente sobre el usuario asociado.
     * </p>
     *
     * @param businessId identificador del negocio.
     * @param name nuevo nombre o razón social (opcional).
     * @param description nueva descripción del negocio (opcional).
     * @param avatarUrl nueva URL del avatar o logo (opcional).
     * @return entidad {@link Business} actualizada.
     * @throws RuntimeException si el negocio no existe.
     */
    @Override
    public Business updateBusinessInfo(Long businessId, String name, String description, String avatarUrl) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        User user = business.getUser();
        if (name != null) user.setName(name);
        if (description != null) user.setDescription(description);
        if (avatarUrl != null) user.setAvatarUrl(avatarUrl);

        userRepository.save(user);
        return business;
    }

    /**
     * Recupera un negocio por su identificador.
     *
     * @param businessId identificador único del negocio.
     * @return un {@link Optional} con el negocio, si existe.
     */
    @Override
    public Optional<Business> getBusinessById(Long businessId) {
        return businessRepository.findById(businessId);
    }

    /**
     * Obtiene la lista completa de horarios asociados a un negocio.
     *
     * @param businessId identificador del negocio.
     * @return lista de horarios de operación del negocio.
     */
    @Override
    public List<BusinessHour> getBusinessHours(Long businessId) {
        return hourRepository.findByBusinessId(businessId);
    }
}