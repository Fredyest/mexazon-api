package com.mexazon.app.service;

import com.mexazon.app.model.BusinessHour;
import com.mexazon.app.model.BusinessHourId;

import java.util.List;
import java.util.Optional;

/**
 * Servicio responsable de gestionar la lógica de negocio relacionada con los horarios
 * de operación de los negocios registrados en Mexazón.
 * <p>
 * Define las operaciones CRUD principales sobre la tabla {@code business_hours},
 * tanto para manipulación individual como masiva de registros.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Abstrae la lógica de persistencia del repositorio {@code BusinessHourRepository}
 *       para permitir una capa intermedia desacoplada y reutilizable.</li>
 *   <li>Permite la actualización y eliminación de horarios a nivel individual o por negocio completo.</li>
 *   <li>Usa {@link BusinessHourId} como identificador compuesto (businessId + dayOfWeek).</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Registrar o actualizar horarios de un negocio.</li>
 *   <li>Consultar horarios existentes por negocio o por día.</li>
 *   <li>Eliminar horarios específicos o todos los de un negocio.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Guardar un horario individual
 * BusinessHour h = new BusinessHour();
 * h.setBusinessId(5L);
 * h.setDayOfWeek("Mon");
 * h.setTimeIn(LocalTime.of(9, 0));
 * h.setTimeOut(LocalTime.of(18, 0));
 * businessHourService.save(h);
 *
 * // Obtener todos los horarios de un negocio
 * List&lt;BusinessHour&gt; horarios = businessHourService.getByBusinessId(5L);
 *
 * // Eliminar todos los horarios de un negocio
 * businessHourService.deleteByBusinessId(5L);
 * </pre>
 */
public interface BusinessHourService {

    /**
     * Crea o actualiza un horario individual de un negocio.
     *
     * @param businessHour entidad {@link BusinessHour} a guardar.
     * @return la entidad guardada o actualizada.
     */
    BusinessHour save(BusinessHour businessHour);

    /**
     * Crea o actualiza múltiples horarios a la vez para un negocio.
     *
     * @param businessHours lista de horarios a guardar.
     * @return lista de horarios guardados.
     */
    List<BusinessHour> saveAll(List<BusinessHour> businessHours);

    /**
     * Obtiene todos los horarios registrados para un negocio específico.
     *
     * @param businessId identificador del negocio.
     * @return lista de horarios asociados al negocio.
     */
    List<BusinessHour> getByBusinessId(Long businessId);

    /**
     * Recupera un horario específico mediante su identificador compuesto.
     *
     * @param id objeto {@link BusinessHourId} con el identificador del horario.
     * @return un {@link Optional} con el horario si existe.
     */
    Optional<BusinessHour> getById(BusinessHourId id);

    /**
     * Elimina un horario específico mediante su identificador compuesto.
     *
     * @param id identificador compuesto del horario a eliminar.
     */
    void deleteById(BusinessHourId id);

    /**
     * Elimina todos los horarios registrados para un negocio.
     *
     * @param businessId identificador del negocio cuyos horarios se eliminarán.
     */
    void deleteByBusinessId(Long businessId);
}