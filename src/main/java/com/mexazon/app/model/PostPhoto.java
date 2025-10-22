package com.mexazon.app.model;

import jakarta.persistence.*;

/**
 * Representa una fotografía asociada a una reseña ({@link Post}) en Mexazón.
 * <p>
 * Cada reseña puede incluir varias imágenes, y cada imagen tiene un orden
 * dentro del conjunto ({@code photoOrder}) para determinar la secuencia de presentación.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>El par {@code (post_id, photo_order)} es único, asegurando que no existan
 *       dos fotos con el mismo orden dentro de una misma reseña.</li>
 *   <li>La relación con {@link Post} se define como {@code ManyToOne} con carga perezosa ({@code LAZY})
 *       para optimizar el rendimiento en consultas que no requieran las imágenes.</li>
 *   <li>El campo {@code photoOrder} se inicializa en 1 por defecto y se utiliza
 *       para mantener el orden visual o cronológico de las fotos asociadas.</li>
 *   <li>El índice {@code idx_post_photos_post} mejora el rendimiento de consultas
 *       que buscan fotos por publicación y orden.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code post_photos}</p>
 */
@Entity
@Table(
    name = "post_photos",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_post_photo_order",
        columnNames = {"post_id", "photo_order"}
    ),
    indexes = @Index(name = "idx_post_photos_post", columnList = "post_id, photo_order")
)
public class PostPhoto {

    /**
     * Identificador único de la fotografía.
     * <p>
     * Se genera automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    /**
     * Reseña (post) a la que pertenece esta fotografía.
     * <p>
     * Se utiliza {@link FetchType#LAZY} para evitar cargar las reseñas completas
     * al consultar únicamente las fotos.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * URL de la imagen asociada a la reseña.
     * <p>
     * Puede apuntar a un recurso almacenado en un bucket, CDN o servidor de archivos.
     * </p>
     */
    @Column(name = "photo_url", length = 255, nullable = false)
    private String photoUrl;

    /**
     * Orden de aparición de la foto dentro del conjunto de imágenes de la reseña.
     * <p>
     * Por ejemplo, la foto con {@code photoOrder = 1} será la principal o portada.
     * </p>
     */
    @Column(name = "photo_order", nullable = false)
    private Integer photoOrder = 1;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador único de la foto. */
    public Long getPhotoId() { return photoId; }

    /** @return Reseña a la que pertenece esta foto. */
    public Post getPost() { return post; }

    /** @param post asigna la reseña a la que pertenece esta foto. */
    public void setPost(Post post) { this.post = post; }

    /** @return URL de la imagen. */
    public String getPhotoUrl() { return photoUrl; }

    /** @param photoUrl define la URL de la imagen asociada. */
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    /** @return Orden en que se muestra la foto dentro de la reseña. */
    public Integer getPhotoOrder() { return photoOrder; }

    /** @param photoOrder establece el orden de la foto dentro de la reseña. */
    public void setPhotoOrder(Integer photoOrder) { this.photoOrder = photoOrder; }
}