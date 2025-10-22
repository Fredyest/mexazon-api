package com.mexazon.app.repository;

import com.mexazon.app.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Post}.
 * <p>
 * Gestiona las reseñas (posts) realizadas por los usuarios hacia los negocios en Mexazón.
 * Además de las operaciones CRUD básicas, incluye consultas personalizadas para
 * validaciones, paginación y cálculos de estadísticas como el promedio y la distribución
 * de calificaciones por negocio.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para obtener las operaciones CRUD estándar.</li>
 *   <li>Incluye métodos derivados para búsquedas por negocio o por autor, con orden descendente por fecha de creación.</li>
 *   <li>Utiliza paginación ({@link Pageable}) para optimizar el rendimiento en listados grandes.</li>
 *   <li>Incluye consultas JPQL personalizadas para obtener métricas de calificación (promedio, conteo, distribución).</li>
 *   <li>El método {@code existsByAuthorUserIdAndReviewedBusinessId} garantiza la unicidad de reseñas por usuario-negocio.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Verificar si un usuario ya reseñó un negocio
 * boolean yaExiste = postRepository.existsByAuthorUserIdAndReviewedBusinessId(2L, 5L);
 *
 * // Obtener reseñas de un negocio paginadas y ordenadas
 * Page&lt;Post&gt; reseñas = postRepository.findByReviewedBusinessIdOrderByCreatedAtDesc(5L, PageRequest.of(0, 10));
 *
 * // Calcular promedio de calificación de un negocio
 * Double promedio = postRepository.avgRatingByBusiness(5L);
 *
 * // Obtener distribución de calificaciones (ej. 5 estrellas, 4 estrellas, etc.)
 * List&lt;Object[]&gt; distribucion = postRepository.distributionByBusiness(5L);
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code Post}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Verifica si un usuario ya ha publicado una reseña para un negocio específico.
     *
     * @param authorUserId identificador del usuario autor de la reseña.
     * @param reviewedBusinessId identificador del negocio reseñado.
     * @return {@code true} si ya existe una reseña para esa combinación usuario/negocio.
     */
    boolean existsByAuthorUserIdAndReviewedBusinessId(Long authorUserId, Long reviewedBusinessId);

    /**
     * Recupera las reseñas asociadas a un negocio, ordenadas por fecha de creación descendente.
     * <p>Utiliza paginación para optimizar rendimiento.</p>
     *
     * @param businessId identificador del negocio.
     * @param pageable parámetros de paginación (número de página y tamaño).
     * @return página de reseñas ordenadas más recientes primero.
     */
    Page<Post> findByReviewedBusinessIdOrderByCreatedAtDesc(Long businessId, Pageable pageable);

    /**
     * Recupera las reseñas publicadas por un usuario específico, ordenadas de más reciente a más antigua.
     *
     * @param authorUserId identificador del autor.
     * @param pageable parámetros de paginación.
     * @return página de reseñas creadas por el usuario.
     */
    Page<Post> findByAuthorUserIdOrderByCreatedAtDesc(Long authorUserId, Pageable pageable);

    /**
     * Obtiene todas las reseñas de la plataforma, ordenadas cronológicamente (descendente).
     *
     * @param pageable parámetros de paginación.
     * @return página de todas las reseñas existentes, ordenadas por fecha.
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // =====================
    // Consultas personalizadas de métricas
    // =====================

    /**
     * Calcula el promedio de calificación (rating) de un negocio.
     *
     * @param businessId identificador del negocio.
     * @return promedio de calificación (entre 1 y 5). Puede ser {@code null} si no hay reseñas.
     */
    @Query("select avg(p.rating) from Post p where p.reviewedBusinessId = :businessId")
    Double avgRatingByBusiness(Long businessId);

    /**
     * Cuenta cuántas reseñas totales tiene un negocio.
     *
     * @param businessId identificador del negocio.
     * @return número total de reseñas asociadas.
     */
    @Query("select count(p) from Post p where p.reviewedBusinessId = :businessId")
    Long countByBusiness(Long businessId);

    /**
     * Obtiene la distribución de calificaciones de un negocio,
     * agrupando por cada nivel de rating (1 a 5).
     * <p>
     * Cada elemento del resultado es un arreglo de objetos donde:
     * <ul>
     *   <li>[0] → calificación (Short)</li>
     *   <li>[1] → cantidad de reseñas con esa calificación (Long)</li>
     * </ul>
     * </p>
     *
     * @param businessId identificador del negocio.
     * @return lista de pares {@code (rating, count)} con la distribución de calificaciones.
     */
    @Query("select p.rating as rating, count(p) as cnt "
         + "from Post p where p.reviewedBusinessId = :businessId "
         + "group by p.rating")
    java.util.List<Object[]> distributionByBusiness(Long businessId);
}