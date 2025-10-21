package com.mexazon.app.service.impl;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.PostalCodeId;
import com.mexazon.app.repository.PostalCodeCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión del catálogo de códigos postales ({@link PostalCodeCatalog}).
 * <p>
 * Proporciona operaciones CRUD básicas para consultar, registrar, actualizar y eliminar
 * registros de códigos postales, alcaldías y colonias.
 * <br>
 * La entidad utiliza una clave compuesta ({@link PostalCodeId}) conformada por
 * el código postal y la colonia.
 */
@Service
public class PostalCodeCatalogService {

    @Autowired
    private PostalCodeCatalogRepository repository;

    // ---------- READ ----------
    /**
     * Obtiene todos los registros del catálogo de códigos postales.
     *
     * @return Lista completa de registros de {@link PostalCodeCatalog}
     */
    @Transactional(readOnly = true)
    public List<PostalCodeCatalog> findAll() {
        return repository.findAll();
    }

    /**
     * Busca un registro específico en el catálogo a partir del código postal y la colonia.
     *
     * @param postalCode Código postal
     * @param colonia Nombre de la colonia
     * @return Un {@link Optional} que contiene el registro si existe
     */
    @Transactional(readOnly = true)
    public Optional<PostalCodeCatalog> findById(String postalCode, String colonia) {
        PostalCodeId id = new PostalCodeId();
        id.setPostalCode(postalCode);
        id.setColonia(colonia);
        return repository.findById(id);
    }

    // ---------- CREATE ----------
    /**
     * Registra un nuevo código postal en el catálogo.
     * <p>
     * Este método puede complementarse con validaciones adicionales,
     * como la verificación de duplicados o consistencia de datos.
     *
     * @param postalCodeCatalog Objeto {@link PostalCodeCatalog} a registrar
     * @return El registro guardado
     */
    @Transactional
    public PostalCodeCatalog save(PostalCodeCatalog postalCodeCatalog) {
        // Validaciones o lógica de negocio antes de guardar
        return repository.save(postalCodeCatalog);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza los datos de un registro existente en el catálogo.
     * <p>
     * La búsqueda se realiza mediante la clave compuesta (código postal + colonia).
     * Solo se actualizan los campos relevantes definidos en el modelo.
     *
     * @param postalCode Código postal del registro a actualizar
     * @param colonia Colonia del registro a actualizar
     * @param updatedPostalCodeCatalog Objeto con los nuevos valores
     * @return El registro actualizado
     * @throws IllegalArgumentException si el registro no existe
     */
    @Transactional
    public PostalCodeCatalog update(String postalCode, String colonia, PostalCodeCatalog updatedPostalCodeCatalog) {
        PostalCodeId id = new PostalCodeId();
        id.setPostalCode(postalCode);
        id.setColonia(colonia);

        Optional<PostalCodeCatalog> existing = repository.findById(id);

        if (existing.isPresent()) {
            PostalCodeCatalog entity = existing.get();

            // Actualiza los campos relevantes según el modelo
            entity.setAlcaldia(updatedPostalCodeCatalog.getAlcaldia());
            // Agrega más setters si el modelo incluye nuevos campos

            return repository.save(entity);
        } else {
            throw new IllegalArgumentException("PostalCodeCatalog no encontrado con id " + id);
        }
    }

    // ---------- DELETE ----------
    /**
     * Elimina un registro del catálogo según su código postal y colonia.
     *
     * @param postalCode Código postal del registro a eliminar
     * @param colonia Colonia del registro a eliminar
     */
    @Transactional
    public void delete(String postalCode, String colonia) {
        PostalCodeId id = new PostalCodeId();
        id.setPostalCode(postalCode);
        id.setColonia(colonia);
        repository.deleteById(id);
    }
}