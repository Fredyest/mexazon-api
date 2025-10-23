package com.mexazon.app.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO (Data Transfer Object) que representa el resumen estadístico
 * de calificaciones (ratings) asociadas a un negocio.
 * <p>
 * Se utiliza principalmente en el endpoint:
 * <pre>
 * GET /api/businesses/{businessId}/rating
 * </pre>
 * para mostrar el promedio de estrellas, el total de reseñas
 * y la distribución de calificaciones de 1 a 5 estrellas.
 * </p>
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Transportar el promedio general de calificaciones de un negocio.</li>
 *   <li>Indicar el número total de reseñas registradas.</li>
 *   <li>Describir la distribución detallada por valor de rating (1 a 5).</li>
 * </ul>
 *
 * <h3>Campos:</h3>
 * <ul>
 *   <li><b>businessId</b>: identificador único del negocio.</li>
 *   <li><b>averageRating</b>: promedio de calificación (1–5) con uno o dos decimales.</li>
 *   <li><b>reviewsCount</b>: número total de reseñas registradas.</li>
 *   <li><b>distribution</b>: mapa que contiene la cantidad de reseñas por cada valor de rating
 *       (por ejemplo: <code>{1: 3, 2: 0, 3: 4, 4: 10, 5: 22}</code>).</li>
 * </ul>
 *
 * <h3>Ejemplo de respuesta JSON:</h3>
 * <pre>
 * {
 *   "businessId": 7,
 *   "averageRating": 4.6,
 *   "reviewsCount": 39,
 *   "distribution": {
 *     "1": 1,
 *     "2": 2,
 *     "3": 5,
 *     "4": 10,
 *     "5": 21
 *   }
 * }
 * </pre>
 *
 * <h3>Origen:</h3>
 * Este DTO es generado por {@link com.mexazon.app.service.PostService#getBusinessRating(Long)}
 * y utilizado por el endpoint
 * {@link com.mexazon.app.controller.PostController#rating(Long)}.
 */
public class BusinessRatingResponse {

    /** Identificador único del negocio. */
    public Long businessId;

    /** Promedio de calificaciones (1–5), con uno o dos decimales. */
    public BigDecimal averageRating;

    /** Número total de reseñas registradas para el negocio. */
    public Long reviewsCount;

    /** Distribución de calificaciones, donde la clave representa el número de estrellas. */
    public Map<Integer, Long> distribution;
}