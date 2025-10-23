package com.mexazon.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una reseña (post) que un usuario realiza sobre un negocio en Mexazón.
 * <p>
 * Un post puede incluir una calificación, una descripción y varias fotografías asociadas.
 * Cada usuario puede publicar **solo una reseña por negocio**, lo cual se garantiza
 * mediante la restricción única {@code uq_author_business_once}.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Se impone la restricción {@code author_user_id + reviewed_business_id} como clave única
 *       para evitar duplicidad de reseñas por usuario/negocio.</li>
 *   <li>Los índices {@code idx_posts_author_created} y {@code idx_posts_business_created}
 *       permiten ordenar y consultar eficientemente las reseñas por autor o por negocio.</li>
 *   <li>El campo {@code createdAt} se genera automáticamente con {@link CreationTimestamp}.</li>
 *   <li>La relación {@code photos} se maneja en cascada y con eliminación automática 
 *       ({@code orphanRemoval = true}) para simplificar mantenimiento.</li>
 *   <li>Se usa {@code rating} como {@link Short} para valores de 1 a 5, representando la calificación del usuario.</li>
 * </ul>
 *
 * <h3>Relaciones principales:</h3>
 * <ul>
 *   <li>Uno a muchos con {@link PostPhoto}: una reseña puede tener varias fotos asociadas.</li>
 *   <li>Muchos a uno (lógico) con {@code User} y {@code Business}, aunque aquí solo se almacenan los IDs.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code posts}</p>
 */
@Entity
@Table(
    name = "posts",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_author_business_once",
        columnNames = {"author_user_id", "reviewed_business_id"}
    ),
    indexes = {
        @Index(name = "idx_posts_author_created", columnList = "author_user_id, created_at"),
        @Index(name = "idx_posts_business_created", columnList = "reviewed_business_id, created_at")
    }
)
public class Post {
<<<<<<< HEAD
=======

    /**
     * Identificador único de la reseña.
     * <p>
     * Se genera automáticamente mediante {@link GenerationType#IDENTITY}.
     * </p>
     */
>>>>>>> b2ca499d4ac5348bc4caf5ec0f8daa26e9443afc
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    /**
     * Identificador del usuario que publica la reseña.
     * <p>
     * Corresponde a la columna {@code user_id} en la tabla {@code users}.
     * </p>
     */
    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    /**
     * Identificador del negocio que está siendo reseñado.
     * <p>
     * Corresponde a la columna {@code business_id} en la tabla {@code businesses}.
     * </p>
     */
    @Column(name = "reviewed_business_id", nullable = false)
    private Long reviewedBusinessId;

    /**
     * Calificación otorgada al negocio, en una escala del 1 al 5.
     */
    @Column(name = "rating", nullable = false)
    private Short rating;

    /**
     * Texto descriptivo de la reseña.
     * <p>
     * Puede incluir comentarios, observaciones o recomendaciones del usuario.
     * </p>
     */
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    /**
     * Fecha y hora en que se creó la reseña.
     * <p>
     * Se genera automáticamente por Hibernate al insertar el registro.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * Lista de fotografías asociadas a la reseña.
     * <p>
     * Se utiliza {@link CascadeType#ALL} y {@code orphanRemoval = true} para que
     * las fotos se creen, actualicen o eliminen junto con la reseña.
     * </p>
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("photoOrder ASC")
    private List<PostPhoto> photos = new ArrayList<>();

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador único de la reseña. */
    public Long getPostId() { return postId; }

    /** @return Identificador del usuario que creó la reseña. */
    public Long getAuthorUserId() { return authorUserId; }

    /** @param authorUserId asigna el identificador del autor. */
    public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }

    /** @return Identificador del negocio reseñado. */
    public Long getReviewedBusinessId() { return reviewedBusinessId; }

    /** @param reviewedBusinessId asigna el identificador del negocio reseñado. */
    public void setReviewedBusinessId(Long reviewedBusinessId) { this.reviewedBusinessId = reviewedBusinessId; }

    /** @return Calificación otorgada al negocio. */
    public Short getRating() { return rating; }

    /** @param rating define la calificación de la reseña (1 a 5). */
    public void setRating(Short rating) { this.rating = rating; }

    /** @return Texto de la reseña. */
    public String getDescription() { return description; }

    /** @param description establece el texto de la reseña. */
    public void setDescription(String description) { this.description = description; }

    /** @return Fecha y hora de creación de la reseña. */
    public LocalDateTime getCreatedAt() { return createdAt; }
<<<<<<< HEAD
    
}
=======

    /** @return Lista de fotos asociadas a esta reseña. */
    public List<PostPhoto> getPhotos() { return photos; }

    /** @param photos asigna la lista de fotos asociadas. */
    public void setPhotos(List<PostPhoto> photos) { this.photos = photos; }
}
>>>>>>> b2ca499d4ac5348bc4caf5ec0f8daa26e9443afc
