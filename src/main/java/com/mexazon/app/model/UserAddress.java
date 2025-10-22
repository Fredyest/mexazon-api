package com.mexazon.app.model;

import jakarta.persistence.*;

/**
 * Representa la dirección única asociada a un usuario dentro de Mexazón.
 * <p>
 * Esta entidad guarda la información de ubicación del usuario (domicilio particular o del negocio),
 * enlazada directamente con el catálogo {@link PostalCodeCatalog} para garantizar integridad
 * y consistencia de los datos geográficos.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>El campo {@code userId} actúa como clave primaria, reflejando una relación uno a uno con {@link User}.</li>
 *   <li>Los campos {@code postalCode} y {@code colonia} se utilizan tanto como datos locales
 *       como claves foráneas hacia {@link PostalCodeCatalog}.</li>
 *   <li>El índice {@code idx_ua_cp_colonia} mejora las consultas por código postal y colonia.</li>
 *   <li>El campo {@code catalogRef} permite acceder fácilmente a la alcaldía y otros metadatos geográficos.</li>
 *   <li>Los atributos {@code street} y {@code number} se mantienen opcionales para flexibilidad.</li>
 * </ul>
 *
 * <h3>Relaciones principales:</h3>
 * <ul>
 *   <li>Uno a uno con {@link User}: cada usuario tiene una sola dirección registrada.</li>
 *   <li>Muchos a uno con {@link PostalCodeCatalog}: varias direcciones pueden referirse al mismo CP y colonia.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code users_address}</p>
 */
@Entity
@Table(
    name = "users_address",
    indexes = @Index(name = "idx_ua_cp_colonia", columnList = "postal_code, colonia")
)
public class UserAddress {

    /**
     * Identificador del usuario propietario de la dirección.
     * <p>
     * Actúa como clave primaria de esta tabla y se asocia directamente a la entidad {@link User}.
     * </p>
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * Código postal asociado a la dirección.
     * <p>
     * Forma parte de la relación con {@link PostalCodeCatalog}.
     * </p>
     */
    @Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;

    /**
     * Colonia correspondiente al código postal.
     * <p>
     * También forma parte de la clave compuesta hacia {@link PostalCodeCatalog}.
     * </p>
     */
    @Column(name = "colonia", length = 100, nullable = false)
    private String colonia;

    /**
     * Nombre de la calle donde reside o se ubica el negocio.
     */
    @Column(name = "street", length = 100)
    private String street;

    /**
     * Número exterior o interior del domicilio.
     */
    @Column(name = "number", length = 10)
    private String number;

    /**
     * Referencia al catálogo de códigos postales, colonias y alcaldías.
     * <p>
     * Se define mediante una relación {@link ManyToOne} con clave compuesta
     * ({@code postal_code}, {@code colonia}). Los campos son solo de lectura en esta entidad
     * (no insertables ni actualizables) para preservar integridad referencial.
     * </p>
     */
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "postal_code", referencedColumnName = "postal_code", insertable = false, updatable = false),
        @JoinColumn(name = "colonia", referencedColumnName = "colonia", insertable = false, updatable = false)
    })
    private PostalCodeCatalog catalogRef;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador del usuario propietario de la dirección. */
    public Long getUserId() { return userId; }

    /** @param userId define el identificador del usuario propietario. */
    public void setUserId(Long userId) { this.userId = userId; }

    /** @return Código postal asociado a la dirección. */
    public String getPostalCode() { return postalCode; }

    /** @param postalCode asigna el código postal. */
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    /** @return Nombre de la colonia. */
    public String getColonia() { return colonia; }

    /** @param colonia asigna el nombre de la colonia. */
    public void setColonia(String colonia) { this.colonia = colonia; }

    /** @return Nombre de la calle. */
    public String getStreet() { return street; }

    /** @param street define la calle de la dirección. */
    public void setStreet(String street) { this.street = street; }

    /** @return Número de la dirección (interior o exterior). */
    public String getNumber() { return number; }

    /** @param number establece el número de la dirección. */
    public void setNumber(String number) { this.number = number; }

    /** @return Referencia al catálogo de códigos postales y colonias. */
    public PostalCodeCatalog getCatalogRef() { return catalogRef; }
}