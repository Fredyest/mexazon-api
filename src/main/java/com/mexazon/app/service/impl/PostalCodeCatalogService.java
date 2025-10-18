package com.mexazon.app.service.impl;

import com.mexazon.app.model.PostalCodeCatalog;
import java.util.List;

/**
 * Service interface that defines high-level operations
 * for working with postal code data.
 */
public interface PostalCodeCatalogService {

    // Get all postal codes
    List<PostalCodeCatalog> getAll();

    // Find postal codes by postal code number
    List<PostalCodeCatalog> getByCodigoPostal(String codigoPostal);

    // Find postal codes by alcaldía
    List<PostalCodeCatalog> getByAlcaldia(String alcaldia);

    // Find postal codes by colonia
    List<PostalCodeCatalog> getByColonia(String colonia);
}
