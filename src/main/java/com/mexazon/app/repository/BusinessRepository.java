package com.mexazon.app.repository;

import com.mexazon.app.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Business}.
 * <p>
 * Permite acceder y manipular los registros de negocios registrados en Mexazón.
 * Hereda todas las operaciones CRUD básicas de {@link JpaRepository}.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>La clave primaria de {@link Business} es {@code businessId}, de tipo {@link Long}.</li>
 *   <li>El repositorio utiliza la convención de Spring Data JPA, lo que permite crear consultas 
 *       personalizadas mediante el nombre de los métodos si es necesario.</li>
 *   <li>El negocio comparte su ID con el {@link com.mexazon.app.model.User}, por lo que 
 *       las operaciones CRUD se mantienen sincronizadas con la tabla {@code users}.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Buscar negocio por su ID
 * Optional&lt;Business&gt; business = businessRepository.findById(10L);
 *
 * // Guardar un nuevo negocio
 * Business newBusiness = new Business();
 * newBusiness.setUser(user);
 * businessRepository.save(newBusiness);
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code Business}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {}