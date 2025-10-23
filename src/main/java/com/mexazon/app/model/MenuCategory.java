package com.mexazon.app.model;

import jakarta.persistence.*;

/**
 * Representa una categoría de menú disponible en la plataforma Mexazón.
 * <p>
 * Las categorías permiten agrupar los platillos de un negocio en secciones
 * (por ejemplo: “Tacos”, “Bebidas”, “Postres”, “Combos”, etc.).
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>La tabla {@code menu_categories} se utiliza como catálogo de referencia.</li>
 *   <li>Los datos se cargan desde el backend o mediante scripts de inicialización (seed), 
 *       ya que no se espera que los usuarios modifiquen estas categorías desde el frontend.</li>
 *   <li>Se aplica una restricción única sobre {@code category_name} para evitar duplicados.</li>
 *   <li>El campo {@code categoryName} es obligatorio y limitado a 50 caracteres.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code menu_categories}</p>
 */
@Entity
@Table(
    name = "menu_categories",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_category_name",
        columnNames = "category_name"
    )
)
public class MenuCategory {

    /**
     * Identificador único de la categoría de menú.
     * <p>
     * Se genera automáticamente mediante {@link GenerationType#IDENTITY}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Nombre de la categoría (por ejemplo: “Tacos”, “Bebidas”, “Postres”).
     * <p>
     * Es único en la tabla y no puede ser nulo.
     * </p>
     */
    @Column(name = "category_name", length = 50, nullable = false)
    private String categoryName;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador único de la categoría. */
    public Long getCategoryId() { return categoryId; }

    /** @param categoryId asigna el identificador único de la categoría. */
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    /** @return Nombre de la categoría. */
    public String getCategoryName() { return categoryName; }

    /** @param categoryName define el nombre de la categoría. */
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}