package com.mexazon.app.service;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.BusinessHour;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los negocios
 * registrados en la plataforma Mexazón.
 * <p>
 * Este servicio coordina la creación, actualización y consulta de negocios,
 * así como la gestión de sus horarios asociados.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Abstrae la interacción entre las entidades {@link Business} y {@link BusinessHour}.</li>
 *   <li>Permite la creación de un negocio junto con sus horarios en una única operación transaccional.</li>
 *   <li>Separa la actualización de información del negocio (nombre, descripción, avatar)
 *       de otros aspectos más complejos como menús o publicaciones.</li>
 *   <li>Utiliza {@link Optional} para representar la existencia o ausencia de registros.</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Registrar un nuevo negocio y sus horarios.</li>
 *   <li>Actualizar la información general del negocio.</li>
 *   <li>Obtener detalles del negocio, incluyendo sus horarios.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear un nuevo negocio con horarios
 * Business b = new Business();
 * b.setUser(user);
 * List&lt;BusinessHour&gt; hours = generateDefaultHours();
 * businessService.createBusiness(b, hours);
 *
 * // Actualizar información del negocio
 * businessService.updateBusinessInfo(10L, "Taquería Don Pepe", "Especialidad en tacos al pastor", avatarUrl);
 *
 * // Consultar negocio
 * Optional&lt;Business&gt; negocio = businessService.getBusinessById(10L);
 *
 * // Consultar horarios del negocio
 * List&lt;BusinessHour&gt; horarios = businessService.getBusinessHours(10L);
 * </pre>
 */
public interface BusinessService {

    /**
     * Crea un nuevo negocio junto con sus horarios de operación.
     * <p>
     * Esta operación suele ser transaccional: primero se crea el negocio
     * y posteriormente se registran los horarios asociados.
     * </p>
     *
     * @param business entidad {@link Business} a registrar.
     * @param hours lista de horarios del negocio.
     * @return el negocio creado.
     */
    Business createBusiness(Business business, List<BusinessHour> hours);

    /**
     * Actualiza la información general del negocio (nombre, descripción, avatar).
     *
     * @param businessId identificador del negocio.
     * @param name nuevo nombre o razón social.
     * @param description nueva descripción del negocio.
     * @param avatarUrl URL de la imagen o logo del negocio.
     * @return entidad {@link Business} actualizada.
     */
    Business updateBusinessInfo(Long businessId, String name, String description, String avatarUrl);

    /**
     * Recupera la información completa de un negocio, incluyendo su estado.
     *
     * @param businessId identificador único del negocio.
     * @return un {@link Optional} conteniendo el negocio, si existe.
     */
    Optional<Business> getBusinessById(Long businessId);

    /**
     * Obtiene la lista completa de horarios de un negocio.
     *
     * @param businessId identificador del negocio.
     * @return lista de horarios asociados.
     */
    List<BusinessHour> getBusinessHours(Long businessId);
}