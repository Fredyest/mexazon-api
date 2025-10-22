package com.mexazon.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Representa un platillo dentro del menú de un negocio registrado en Mexazón.
 * <p>
 * Cada platillo pertenece a un negocio específico y a una categoría dentro de su menú.
 * Se impone una restricción de unicidad para garantizar que no existan dos platillos
 * con el mismo nombre dentro del mismo negocio.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>La combinación {@code business_id + dish_name} es única en la base de datos 
 *       mediante la restricción {@code uq_business_dishname}.</li>
 *   <li>Se utiliza {@link BigDecimal} para representar precios y evitar pérdida de precisión.</li>
 *   <li>Los índices {@code idx_dish_business} y {@code idx_dish_category_business}
 *       optimizan consultas frecuentes por negocio y categoría.</li>
 *   <li>Los campos de relación ({@code businessId}, {@code categoryId}) se manejan como IDs
 *       para mantener el modelo liviano en esta fase; pueden reemplazarse más adelante por
 *       asociaciones {@link ManyToOne} si se requiere navegación directa.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code dishes}</p>
 */
@Entity
@Table(
    name = "dishes",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_business_dishname",
        columnNames = {"business_id", "dish_name"}
    ),
    indexes = {
        @Index(name = "idx_dish_business", columnList = "business_id"),
        @Index(name = "idx_dish_category_business", columnList = "category_id,business_id")
    }
)
public class Dish {

    /**
     * Identificador único del platillo.
     * <p>
     * Es autogenerado por la base de datos mediante {@link GenerationType#IDENTITY}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_id")
    private Long dishId;

    /**
     * Identificador del negocio al que pertenece este platillo.
     * <p>
     * Relaciona este registro con la tabla {@code businesses}.
     * </p>
     */
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    /**
     * Identificador de la categoría del platillo dentro del menú.
     * <p>
     * Relaciona este registro con la tabla {@code menu_categories}.
     * </p>
     */
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    /**
     * Nombre del platillo, único dentro de un mismo negocio.
     * <p>
     * Se recomienda mantenerlo breve (máximo 100 caracteres).
     * </p>
     */
    @Column(name = "dish_name", length = 100, nullable = false)
    private String dishName;

    /**
     * Descripción opcional del platillo.
     * <p>
     * Puede incluir detalles sobre ingredientes, preparación o notas para el cliente.
     * </p>
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Precio del platillo.
     * <p>
     * Se define con precisión de 10 dígitos y 2 decimales (por ejemplo: 99999999.99).
     * </p>
     */
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * URL de la imagen del platillo.
     * <p>
     * Puede referirse a un recurso almacenado en un CDN o bucket de S3.
     * </p>
     */
    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador único del platillo. */
    public Long getDishId() { return dishId; }

    /** @param dishId asigna el identificador del platillo. */
    public void setDishId(Long dishId) { this.dishId = dishId; }

    /** @return Identificador del negocio al que pertenece. */
    public Long getBusinessId() { return businessId; }

    /** @param businessId define el negocio propietario del platillo. */
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    /** @return Identificador de la categoría del platillo. */
    public Long getCategoryId() { return categoryId; }

    /** @param categoryId define la categoría del platillo. */
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    /** @return Nombre del platillo. */
    public String getDishName() { return dishName; }

    /** @param dishName establece el nombre del platillo. */
    public void setDishName(String dishName) { this.dishName = dishName; }

    /** @return Descripción del platillo. */
    public String getDescription() { return description; }

    /** @param description define la descripción del platillo. */
    public void setDescription(String description) { this.description = description; }

    /** @return Precio del platillo. */
    public BigDecimal getPrice() { return price; }

    /** @param price asigna el precio del platillo. */
    public void setPrice(BigDecimal price) { this.price = price; }

    /** @return URL de la foto del platillo. */
    public String getPhotoUrl() { return photoUrl; }

    /** @param photoUrl establece la URL de la foto del platillo. */
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}