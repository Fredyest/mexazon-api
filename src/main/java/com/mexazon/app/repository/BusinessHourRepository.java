package com.mexazon.app.repository;

import com.mexazon.app.model.BusinessHour;
import com.mexazon.app.model.BusinessHourId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link BusinessHour}.
 * <p>
 * Proporciona operaciones CRUD y consultas personalizadas para los horarios
 * de los negocios registrados en Mexazón.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar métodos estándar de persistencia 
 *       (findAll, findById, save, delete, etc.).</li>
 *   <li>Utiliza {@link BusinessHourId} como clave primaria compuesta.</li>
 *   <li>Define una consulta derivada para obtener todos los horarios asociados a un negocio específico.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener los horarios de un negocio con ID = 5
 * List&lt;BusinessHour&gt; hours = businessHourRepository.findByBusinessId(5L);
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code BusinessHour}</p>
 * <p><strong>Clave primaria:</strong> {@code BusinessHourId}</p>
 */
@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, BusinessHourId> {

    /**
     * Recupera todos los horarios registrados para un negocio específico.
     *
     * @param businessId identificador único del negocio.
     * @return lista de horarios ({@link BusinessHour}) asociados al negocio.
     */
    List<BusinessHour> findByBusinessId(Long businessId);
}