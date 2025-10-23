package com.mexazon.app.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) que representa el cuerpo de la solicitud
 * para crear una nueva reseña (post) dentro del sistema.
 * <p>
 * Este objeto se envía al endpoint:
 * <pre>
 * POST /api/posts
 * </pre>
 * e incluye información del autor, el negocio reseñado, el texto descriptivo,
 * la calificación y, de manera opcional, un conjunto de fotografías asociadas.
 * </p>
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Transportar los datos necesarios para registrar una reseña completa.</li>
 *   <li>Permitir el envío de fotografías opcionales en el mismo payload.</li>
 *   <li>Garantizar la estructura esperada por el backend antes de la conversión a entidad.</li>
 * </ul>
 *
 * <h3>Campos:</h3>
 * <ul>
 *   <li><b>authorUserId</b>: ID del usuario que publica la reseña.</li>
 *   <li><b>reviewedBusinessId</b>: ID del negocio que está siendo reseñado.</li>
 *   <li><b>rating</b>: calificación entre 1 y 5 estrellas (obligatoria).</li>
 *   <li><b>description</b>: texto libre con la opinión o comentario del usuario.</li>
 *   <li><b>photos</b>: lista opcional de fotografías que acompañan la reseña.</li>
 * </ul>
 *
 * <h3>Formato esperado en la solicitud JSON:</h3>
 * <pre>
 * {
 *   "authorUserId": 4,
 *   "reviewedBusinessId": 12,
 *   "rating": 5,
 *   "description": "Deliciosos tacos con piña 🍍",
 *   "photos": [
 *     { "photoUrl": "https://cdn.mexazon.com/uploads/tacos1.jpg", "photoOrder": 1 },
 *     { "photoUrl": "https://cdn.mexazon.com/uploads/tacos2.jpg" }
 *   ]
 * }
 * </pre>
 *
 * <h3>Reglas de negocio:</h3>
 * <ul>
 *   <li>El <code>rating</code> debe ser un valor entre 1 y 5.</li>
 *   <li>Solo puede existir una reseña por usuario-negocio.</li>
 *   <li>El campo <code>photoOrder</code> es opcional; si se omite, el backend asigna el siguiente orden disponible.</li>
 * </ul>
 *
 * @see com.mexazon.app.model.Post
 * @see com.mexazon.app.model.PostPhoto
 * @see com.mexazon.app.controller.PostController
 */
public class CreatePostRequest {

    /** ID del usuario que crea la reseña. */
    public Long authorUserId;

    /** ID del negocio reseñado. */
    public Long reviewedBusinessId;

    /** Calificación de 1 a 5 estrellas. */
    public Short rating;

    /** Texto descriptivo del comentario o reseña. */
    public String description;

    /** Lista opcional de fotografías asociadas al post. */
    public List<PhotoItem> photos;

    /**
     * Representa una fotografía incluida en la creación de la reseña.
     */
    public static class PhotoItem {

        /** URL de la fotografía. */
        public String photoUrl;

        /** Orden opcional de aparición; si es null, el backend lo genera automáticamente. */
        public Integer photoOrder;
    }
}