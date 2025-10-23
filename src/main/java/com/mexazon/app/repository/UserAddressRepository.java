package com.mexazon.app.repository;

import com.mexazon.app.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link UserAddress}.
 * <p>
 * Gestiona las operaciones de persistencia relacionadas con la dirección única
 * de cada usuario en Mexazón. Este repositorio permite realizar operaciones CRUD
 * básicas sobre la tabla {@code users_address}, la cual está vinculada al catálogo
 * de códigos postales {@link com.mexazon.app.model.PostalCodeCatalog}.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar.</li>
 *   <li>La clave primaria es el campo {@code userId}, compartido con la entidad {@link com.mexazon.app.model.User}.</li>
 *   <li>Al ser una relación uno a uno con el usuario, este repositorio permite crear,
 *       actualizar o eliminar la dirección de un usuario de forma directa.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener la dirección de un usuario
 * Optional&lt;UserAddress&gt; address = userAddressRepository.findById(10L);
 *
 * // Guardar o actualizar dirección
 * UserAddress addr = new UserAddress();
 * addr.setUserId(10L);
 * addr.setPostalCode("04360");
 * addr.setColonia("Del Carmen");
 * addr.setStreet("Av. Universidad");
 * addr.setNumber("123");
 * userAddressRepository.save(addr);
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code UserAddress}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {}