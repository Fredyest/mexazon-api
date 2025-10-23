package com.mexazon.app.service.impl;

import com.mexazon.app.model.UserAddress;
import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.repository.PostalCodeCatalogRepository;
import com.mexazon.app.repository.UserAddressRepository;
import com.mexazon.app.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementación del servicio {@link AddressService}.
 * <p>
 * Gestiona la lógica de negocio relacionada con las direcciones de usuario
 * y la consulta del catálogo de códigos postales, colonias y alcaldías.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Consultar las colonias asociadas a un código postal.</li>
 *   <li>Registrar nuevas direcciones de usuario, validando su integridad.</li>
 *   <li>Evitar duplicación de direcciones para un mismo usuario.</li>
 *   <li>Verificar la existencia de códigos postales y colonias en el catálogo.</li>
 *   <li>Recuperar entradas específicas del catálogo.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Usa dos repositorios: {@link PostalCodeCatalogRepository} para datos de catálogo
 *       y {@link UserAddressRepository} para persistencia de direcciones.</li>
 *   <li>Aplica validaciones antes de registrar una nueva dirección, garantizando
 *       que los códigos postales y colonias sean válidos y existentes.</li>
 *   <li>Emplea excepciones semánticas para representar distintos tipos de error:
 *       <ul>
 *         <li>{@link IllegalStateException} → dirección ya existente para el usuario.</li>
 *         <li>{@link NoSuchElementException} → código postal o colonia inexistente.</li>
 *       </ul>
 *   </li>
 *   <li>Devuelve respuestas ligeras en forma de {@link Map} cuando se espera interoperabilidad con APIs REST.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener colonias asociadas a un código postal
 * List&lt;Map&lt;String, String&gt;&gt; colonias = addressService.findColoniasByPostalCode("04360");
 *
 * // Crear una nueva dirección de usuario
 * UserAddress addr = new UserAddress();
 * addr.setUserId(15L);
 * addr.setPostalCode("04360");
 * addr.setColonia("Del Carmen");
 * addr.setStreet("Av. Universidad");
 * addr.setNumber("123");
 * addressService.createAddress(addr);
 *
 * // Recuperar dirección por ID de usuario
 * Optional&lt;UserAddress&gt; userAddress = addressService.getUserAddress(15L);
 *
 * // Consultar una entrada específica del catálogo
 * Map&lt;String, String&gt; info = addressService.getCatalogEntry("04360", "Del Carmen");
 * </pre>
 */
@Service
public class AddressServiceImpl implements AddressService {

    /** Repositorio del catálogo de códigos postales. */
    private final PostalCodeCatalogRepository catalogRepo;

    /** Repositorio de direcciones de usuario. */
    private final UserAddressRepository addressRepo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param catalogRepo repositorio del catálogo postal.
     * @param addressRepo repositorio de direcciones de usuario.
     */
    public AddressServiceImpl(PostalCodeCatalogRepository catalogRepo, UserAddressRepository addressRepo) {
        this.catalogRepo = catalogRepo;
        this.addressRepo = addressRepo;
    }

    /**
     * Obtiene la lista de colonias y alcaldías asociadas a un código postal.
     *
     * @param postalCode código postal (CP) a consultar.
     * @return lista de mapas con las claves {@code colonia} y {@code alcaldia}.
     */
    @Override
    public List<Map<String, String>> findColoniasByPostalCode(String postalCode) {
        List<PostalCodeCatalog> results = catalogRepo.findByPostalCode(postalCode);
        if (results.isEmpty()) return Collections.emptyList();

        List<Map<String, String>> response = new ArrayList<>();
        for (PostalCodeCatalog p : results) {
            response.add(Map.of("colonia", p.getColonia(), "alcaldia", p.getAlcaldia()));
        }
        return response;
    }

    /**
     * Recupera la dirección registrada de un usuario.
     *
     * @param userId identificador del usuario.
     * @return un {@link Optional} con la dirección si existe.
     */
    @Override
    public Optional<UserAddress> getUserAddress(Long userId) {
        return addressRepo.findById(userId);
    }

    /**
     * Crea una nueva dirección de usuario, validando que no exista previamente
     * y que el código postal y colonia sean válidos según el catálogo.
     *
     * @param address entidad {@link UserAddress} a registrar.
     * @return dirección creada y guardada en la base de datos.
     * @throws IllegalStateException si ya existe una dirección registrada para el usuario.
     * @throws NoSuchElementException si el CP o colonia no existen en el catálogo.
     */
    @Override
    public UserAddress createAddress(UserAddress address) {
        // Validar duplicado
        if (addressRepo.existsById(address.getUserId())) {
            throw new IllegalStateException("Address already exists for this user");
        }

        // Validar existencia del CP y colonia
        var id = new com.mexazon.app.model.PostalCodeCatalogId(address.getPostalCode(), address.getColonia());
        if (!catalogRepo.existsById(id)) {
            throw new NoSuchElementException("Postal code or colonia not found");
        }

        return addressRepo.save(address);
    }

    /**
     * Obtiene una entrada específica del catálogo de códigos postales.
     *
     * @param postalCode código postal a consultar.
     * @param colonia nombre de la colonia.
     * @return mapa con los datos de la entrada (CP, colonia y alcaldía).
     * @throws NoSuchElementException si la entrada no existe en el catálogo.
     */
    @Override
    public Map<String, String> getCatalogEntry(String postalCode, String colonia) {
        var id = new com.mexazon.app.model.PostalCodeCatalogId(postalCode, colonia);
        PostalCodeCatalog entry = catalogRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Postal code or colonia not found"));

        return Map.of(
                "postalCode", entry.getPostalCode(),
                "colonia", entry.getColonia(),
                "alcaldia", entry.getAlcaldia()
        );
    }
}