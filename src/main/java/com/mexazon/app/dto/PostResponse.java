package com.mexazon.app.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) utilizado como respuesta
 * para representar una reseña (post) con sus fotografías ordenadas.
 * <p>
 * Este objeto es devuelto por el backend en las operaciones de creación,
 * consulta individual o listados de reseñas.
 * </p>
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Encapsular la información visible de una reseña para el frontend.</li>
 *   <li>Incluir la lista de fotografías asociadas en orden ascendente.</li>
 *   <li>Evitar exponer detalles internos de las entidades JPA (como relaciones o IDs técnicos).</li>
 * </ul>
 *
 * <h3>Campos:</h3>
 * <ul>
 *   <li><b>postId</b>: identificador único de la reseña.</li>
 *   <li><b>authorUserId</b>: ID del usuario que publicó la reseña.</li>
 *   <li><b>reviewedBusinessId</b>: ID del negocio reseñado.</li>
 *   <li><b>rating</b>: calificación numérica (1–5 estrellas).</li>
 *   <li><b>description</b>: comentario o texto escrito por el usuario.</li>
 *   <li><b>createdAt</b>: fecha y hora en que se creó la reseña.</li>
 *   <li><b>photos</b>: lista ordenada de fotos asociadas a la reseña.</li>
 * </ul>
 *
 * <h3>Ejemplo de respuesta JSON:</h3>
 * <pre>
 * {
 *   "postId": 42,
 *   "authorUserId": 4,
 *   "reviewedBusinessId": 12,
 *   "rating": 5,
 *   "description": "Deliciosos tacos con piña 🍍",
 *   "createdAt": "2025-10-21T21:45:00",
 *   "photos": [
 *     { "photoId": 1, "photoUrl": "https://cdn.mexazon.com/tacos1.jpg", "photoOrder": 1 },
 *     { "photoId": 2, "photoUrl": "https://cdn.mexazon.com/tacos2.jpg", "photoOrder": 2 }
 *   ]
 * }
 * </pre>
 *
 * <h3>Origen:</h3>
 * Este DTO es generado por {@link com.mexazon.app.service.impl.PostServiceImpl}
 * y retornado por los endpoints del {@link com.mexazon.app.controller.PostController}.
 */
public class PostResponse {

    /** Identificador único de la reseña. */
    public Long postId;

    /** ID del usuario autor de la reseña. */
    public Long authorUserId;

    /** ID del negocio al que pertenece la reseña. */
    public Long reviewedBusinessId;

    /** Calificación del usuario (1 a 5 estrellas). */
    public Short rating;

    /** Texto del comentario o reseña. */
    public String description;

    /** Fecha y hora en que se creó la reseña. */
    public LocalDateTime createdAt;

    /** Lista ordenada de fotografías asociadas. */
    public List<Photo> photos;

    /**
     * Representa una fotografía asociada al post.
     */
    public static class Photo {

        /** Identificador único de la foto. */
        public Long photoId;

        /** URL pública o relativa de la fotografía. */
        public String photoUrl;

        /** Orden de aparición dentro de la reseña. */
        public Integer photoOrder;
    }
}