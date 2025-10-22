package com.mexazon.app.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) utilizado para agregar nuevas fotografías
 * a una reseña (post) existente.
 * <p>
 * Este objeto se envía al endpoint:
 * <pre>
 * POST /api/posts/{postId}/photos
 * </pre>
 * </p>
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Transportar la lista de fotos a agregar a un post ya creado.</li>
 *   <li>Permitir especificar un orden (<code>photoOrder</code>) opcional para cada imagen.</li>
 *   <li>Si no se especifica el orden, el backend asignará el siguiente consecutivo disponible.</li>
 * </ul>
 *
 * <h3>Formato esperado en la solicitud JSON:</h3>
 * <pre>
 * {
 *   "photos": [
 *     { "photoUrl": "https://cdn.mexazon.com/uploads/photo1.jpg", "photoOrder": 1 },
 *     { "photoUrl": "https://cdn.mexazon.com/uploads/photo2.jpg" }
 *   ]
 * }
 * </pre>
 *
 * <h3>Campos:</h3>
 * <ul>
 *   <li><b>photos</b>: lista de fotografías a agregar.</li>
 *   <li><b>photoUrl</b>: URL pública o relativa de la imagen.</li>
 *   <li><b>photoOrder</b>: número opcional que indica el orden de aparición.</li>
 * </ul>
 *
 * <h3>Ejemplo de respuesta exitosa:</h3>
 * <pre>
 * 201 Created
 * [
 *   { "photoId": 1, "photoUrl": "https://cdn.mexazon.com/uploads/photo1.jpg", "photoOrder": 1 },
 *   { "photoId": 2, "photoUrl": "https://cdn.mexazon.com/uploads/photo2.jpg", "photoOrder": 2 }
 * ]
 * </pre>
 *
 * @see com.mexazon.app.model.PostPhoto
 * @see com.mexazon.app.controller.PostController
 */
public class AddPhotosRequest {

    /** Lista de fotografías a agregar al post. */
    public List<PhotoItem> photos;

    /**
     * Representa cada fotografía individual dentro de la solicitud.
     */
    public static class PhotoItem {

        /** URL de la fotografía a adjuntar. */
        public String photoUrl;

        /** Orden opcional de aparición (si se omite, se calcula automáticamente). */
        public Integer photoOrder;
    }
}