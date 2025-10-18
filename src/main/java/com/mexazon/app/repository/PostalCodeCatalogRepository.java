package com.mexazon.app.repository;

import com.mexazon.app.model.PostalCodeCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations
 * (Create, Read, Update, Delete) on the 'postal_code_catalog' table.
 *
 * It automatically provides methods like:
 *  - save()        → insert or update a record
 *  - findAll()     → get all records
 *  - findById()    → get one record by ID
 *  - deleteById()  → delete a record
 *
 * Plus, we can define custom methods by following JPA naming conventions.
 */
@Repository
public interface PostalCodeCatalogRepository extends JpaRepository<PostalCodeCatalog, Long> {

    /**
     * Custom method to find all postal code records that match a given postal code.
     * Example: findByCodigoPostal("64000")
     */
    List<PostalCodeCatalog> findByCodigoPostal(String codigoPostal);

    /**
     * Custom method to find all postal codes that belong to a specific alcaldía.
     * Example: findByAlcaldia("Monterrey")
     */
    List<PostalCodeCatalog> findByAlcaldia(String alcaldia);

    /**
     * Custom method to find all postal codes within a specific colonia.
     * Example: findByColonia("Centro")
     */
    List<PostalCodeCatalog> findByColonia(String colonia);
}
