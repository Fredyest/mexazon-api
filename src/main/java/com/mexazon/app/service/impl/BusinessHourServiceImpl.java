package com.mexazon.app.service.impl;

import com.mexazon.app.model.BusinessHour;
import com.mexazon.app.model.BusinessHourId;
import com.mexazon.app.repository.BusinessHourRepository;
import com.mexazon.app.service.BusinessHourService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio {@link BusinessHourService}.
 * <p>
 * Gestiona la lógica de negocio relacionada con los horarios de operación
 * de los negocios registrados en Mexazón.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Crear o actualizar horarios individuales o múltiples.</li>
 *   <li>Consultar horarios de un negocio específico.</li>
 *   <li>Eliminar horarios individualmente o todos los de un negocio.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link BusinessHourRepository} como capa de acceso a datos.</li>
 *   <li>Separa la lógica de negocio de la capa de persistencia.</li>
 *   <li>Permite manejar operaciones en lote ({@code saveAll}) para optimizar inserciones masivas.</li>
 *   <li>La eliminación por negocio recupera primero los horarios antes de eliminarlos
 *       para mantener consistencia y permitir validaciones intermedias si se requiere.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear o actualizar un horario
 * BusinessHour h = new BusinessHour();
 * h.setBusinessId(5L);
 * h.setDayOfWeek("Mon");
 * h.setTimeIn(LocalTime.of(9, 0));
 * h.setTimeOut(LocalTime.of(18, 0));
 * businessHourService.save(h);
 *
 * // Consultar todos los horarios de un negocio
 * List&lt;BusinessHour&gt; horarios = businessHourService.getByBusinessId(5L);
 *
 * // Eliminar todos los horarios de un negocio
 * businessHourService.deleteByBusinessId(5L);
 * </pre>
 */
@Service
public class BusinessHourServiceImpl implements BusinessHourService {

    /** Repositorio encargado de las operaciones CRUD sobre los horarios. */
    private final BusinessHourRepository repository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository repositorio JPA para la entidad {@link BusinessHour}.
     */
    public BusinessHourServiceImpl(BusinessHourRepository repository) {
        this.repository = repository;
    }

    /**
     * Guarda o actualiza un horario individual en la base de datos.
     *
     * @param businessHour entidad {@link BusinessHour} a guardar.
     * @return la entidad persistida.
     */
    @Override
    public BusinessHour save(BusinessHour businessHour) {
        return repository.save(businessHour);
    }

    /**
     * Guarda o actualiza múltiples horarios en una sola operación.
     *
     * @param businessHours lista de horarios.
     * @return lista de horarios guardados.
     */
    @Override
    public List<BusinessHour> saveAll(List<BusinessHour> businessHours) {
        return repository.saveAll(businessHours);
    }

    /**
     * Recupera todos los horarios de un negocio.
     *
     * @param businessId identificador del negocio.
     * @return lista de horarios registrados.
     */
    @Override
    public List<BusinessHour> getByBusinessId(Long businessId) {
        return repository.findByBusinessId(businessId);
    }

    /**
     * Recupera un horario específico mediante su identificador compuesto.
     *
     * @param id objeto {@link BusinessHourId} con el identificador del horario.
     * @return un {@link Optional} con el horario, si existe.
     */
    @Override
    public Optional<BusinessHour> getById(BusinessHourId id) {
        return repository.findById(id);
    }

    /**
     * Elimina un horario individual mediante su identificador compuesto.
     *
     * @param id identificador del horario.
     */
    @Override
    public void deleteById(BusinessHourId id) {
        repository.deleteById(id);
    }

    /**
     * Elimina todos los horarios asociados a un negocio específico.
     * <p>
     * Primero recupera la lista de horarios mediante {@link BusinessHourRepository#findByBusinessId(Long)},
     * luego los elimina todos en bloque.
     * </p>
     *
     * @param businessId identificador del negocio.
     */
    @Override
    public void deleteByBusinessId(Long businessId) {
        List<BusinessHour> hours = repository.findByBusinessId(businessId);
        repository.deleteAll(hours);
    }
}