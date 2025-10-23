package com.mexazon.app.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa la clave primaria compuesta de la entidad {@link PostalCodeCatalog}.
 * <p>
 * Define la combinación única de {@code postalCode} y {@code colonia}
 * como identificador de cada registro dentro del catálogo de códigos postales.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Implementa {@link Serializable}, requisito obligatorio para claves compuestas en JPA.</li>
 *   <li>Se definen correctamente los métodos {@code equals()} y {@code hashCode()} 
 *       para garantizar comparaciones consistentes dentro del contexto de persistencia.</li>
 *   <li>No se incluyen anotaciones JPA aquí, ya que esta clase se usa únicamente como identificador auxiliar
 *       referenciado mediante {@link jakarta.persistence.IdClass}.</li>
 * </ul>
 *
 * <p><strong>Uso:</strong> Asociada a {@link PostalCodeCatalog} mediante {@code @IdClass(PostalCodeCatalogId.class)}.</p>
 */
public class PostalCodeCatalogId implements Serializable {

    /** Código postal (parte 1 de la clave compuesta). */
    private String postalCode;

    /** Colonia asociada al código postal (parte 2 de la clave compuesta). */
    private String colonia;

    // =====================
    // Constructores
    // =====================

    /** Constructor vacío requerido por JPA. */
    public PostalCodeCatalogId() {}

    /**
     * Constructor conveniente para crear una instancia del ID compuesto.
     *
     * @param postalCode Código postal.
     * @param colonia    Nombre de la colonia.
     */
    public PostalCodeCatalogId(String postalCode, String colonia) {
        this.postalCode = postalCode;
        this.colonia = colonia;
    }

    // =====================
    // Equals y HashCode
    // =====================

    /**
     * Compara dos instancias de {@link PostalCodeCatalogId} para determinar si representan el mismo registro.
     * <p>
     * Dos objetos son iguales si tienen el mismo {@code postalCode} y la misma {@code colonia}.
     * </p>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostalCodeCatalogId)) return false;
        PostalCodeCatalogId that = (PostalCodeCatalogId) o;
        return Objects.equals(postalCode, that.postalCode)
                && Objects.equals(colonia, that.colonia);
    }

    /**
     * Calcula un hash basado en ambos campos de la clave compuesta.
     * <p>
     * Esto asegura que las instancias puedan usarse correctamente en colecciones
     * como {@link java.util.HashSet} o {@link java.util.Map}.
     * </p>
     */
    @Override
    public int hashCode() {
        return Objects.hash(postalCode, colonia);
    }
}