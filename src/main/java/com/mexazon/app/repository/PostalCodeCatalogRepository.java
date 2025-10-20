package com.mexazon.app.repository;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.PostalCodeId;

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
public interface PostalCodeCatalogRepository extends JpaRepository<PostalCodeCatalog, PostalCodeId> {

	// ✅ Find all by postal code (inside the embedded ID)
    List<PostalCodeCatalog> findAllByIdPostalCode(String postalCode);

    // ✅ Find all by colonia (inside the embedded ID)
    List<PostalCodeCatalog> findAllByIdColonia(String colonia);

    // ✅ Find one by both postal code and colonia (composite key)
    PostalCodeCatalog findByIdPostalCodeAndIdColonia(String postalCode, String colonia);

    // ✅ Find by alcaldia
    List<PostalCodeCatalog> findAllByAlcaldia(String alcaldia);


}
