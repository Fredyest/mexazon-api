package com.mexazon.app.model;

import jakarta.persistence.*;

/**
 * Catálogo de códigos postales, colonias y alcaldías de la CDMX y áreas metropolitanas.
 * <p>
 * Esta entidad funciona como una tabla de referencia (catálogo) que permite relacionar
 * los domicilios de usuarios y negocios con sus respectivas zonas geográficas.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Se define una clave compuesta con {@code postalCode} y {@code colonia} mediante {@link IdClass}.</li>
 *   <li>El catálogo se carga desde el backend (por seed o importación masiva) y se consulta desde el frontend.</li>
 *   <li>No se prevén operaciones de escritura desde el frontend.</li>
 *   <li>Se agrega un índice sobre {@code alcaldia} para acelerar búsquedas por zona administrativa.</li>
 * </ul>
 *
 * <h3>Campos principales:</h3>
 * <ul>
 *   <li>{@code postalCode} — Código postal (clave principal compuesta).</li>
 *   <li>{@code colonia} — Nombre de la colonia asociada al código postal.</li>
 *   <li>{@code alcaldia} — Alcaldía o municipio correspondiente.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code postal_code_catalog}</p>
 */
@Entity
@Table(
    name = "postal_code_catalog",
    indexes = @Index(name = "idx_pcc_alcaldia", columnList = "alcaldia")
)
@IdClass(PostalCodeCatalogId.class)
public class PostalCodeCatalog {

    /**
     * Código postal (CP) que identifica un área geográfica.
     * <p>
     * Forma parte de la clave primaria compuesta junto con {@code colonia}.
     * </p>
     */
    @Id
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /**
     * Nombre de la colonia o barrio.
     * <p>
     * También forma parte de la clave primaria compuesta.
     * </p>
     */
    @Id
    @Column(name = "colonia", length = 100)
    private String colonia;

    /**
     * Alcaldía o municipio asociado al código postal y colonia.
     */
    @Column(name = "alcaldia", length = 100, nullable = false)
    private String alcaldia;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Código postal asociado. */
    public String getPostalCode() { return postalCode; }

    /** @param postalCode define el código postal. */
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    /** @return Nombre de la colonia. */
    public String getColonia() { return colonia; }

    /** @param colonia define el nombre de la colonia. */
    public void setColonia(String colonia) { this.colonia = colonia; }

    /** @return Nombre de la alcaldía o municipio. */
    public String getAlcaldia() { return alcaldia; }

    /** @param alcaldia asigna la alcaldía correspondiente. */
    public void setAlcaldia(String alcaldia) { this.alcaldia = alcaldia; }
}