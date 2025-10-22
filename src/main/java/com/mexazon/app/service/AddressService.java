package com.mexazon.app.service;

import com.mexazon.app.model.UserAddress;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Servicio que gestiona la lógica relacionada con direcciones de usuario
 * y el catálogo de códigos postales en Mexazón.
 * <p>
 * Define las operaciones principales para consultar colonias por código postal,
 * crear direcciones y recuperar información del catálogo de ubicación.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>La interfaz abstrae la lógica de negocio del acceso a datos (repositorios),
 *       permitiendo que distintas implementaciones la utilicen sin afectar al controlador.</li>
 *   <li>El método {@code findColoniasByPostalCode} devuelve estructuras ligeras tipo {@link Map}
 *       para uso directo desde el frontend.</li>
 *   <li>La entidad {@link UserAddress} se gestiona a través de métodos específicos
 *       para creación y consulta.</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Consultar colonias disponibles a partir de un código postal.</li>
 *   <li>Registrar o actualizar direcciones de usuario.</li>
 *   <li>Obtener información del catálogo postal (alcaldía, colonia, etc.).</li>
 *   <li>Retornar resultados en estructuras compatibles con APIs REST (Map o DTOs).</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener colonias de un CP
 * List&lt;Map&lt;String, String&gt;&gt; colonias = addressService.findColoniasByPostalCode("04360");
 *
 * // Obtener dirección de un usuario
 * Optional&lt;UserAddress&gt; address = addressService.getUserAddress(10L);
 *
 * // Crear o actualizar una dirección
 * UserAddress nueva = addressService.createAddress(addressObj);
 *
 * // Obtener entrada del catálogo postal
 * Map&lt;String, String&gt; catalog = addressService.getCatalogEntry("04360", "Del Carmen");
 * </pre>
 */
public interface AddressService {

    /**
     * Obtiene la lista de colonias asociadas a un código postal.
     *
     * @param postalCode código postal (CP) a consultar.
     * @return lista de mapas con nombre de colonia y alcaldía.
     */
    List<Map<String, String>> findColoniasByPostalCode(String postalCode);

    /**
     * Recupera la dirección registrada de un usuario, si existe.
     *
     * @param userId identificador único del usuario.
     * @return un {@link Optional} conteniendo la dirección del usuario, o vacío si no existe.
     */
    Optional<UserAddress> getUserAddress(Long userId);

    /**
     * Crea o actualiza la dirección de un usuario.
     *
     * @param address objeto {@link UserAddress} a persistir.
     * @return la entidad {@link UserAddress} guardada en base de datos.
     */
    UserAddress createAddress(UserAddress address);

    /**
     * Obtiene una entrada del catálogo de códigos postales (CP, colonia, alcaldía).
     *
     * @param postalCode código postal a consultar.
     * @param colonia nombre de la colonia.
     * @return mapa con los datos asociados (CP, colonia, alcaldía).
     */
    Map<String, String> getCatalogEntry(String postalCode, String colonia);
}