package com.mexazon.app.service;

import com.mexazon.app.dto.*;
import org.springframework.data.domain.Page;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con las reseñas
 * ({@link com.mexazon.app.model.Post}) que los usuarios publican sobre los negocios en Mexazón.
 * <p>
 * Proporciona métodos para crear, consultar, listar, eliminar reseñas, y manejar sus fotografías
 * y estadísticas de calificación. Implementa la lógica intermedia entre el controlador REST
 * y los repositorios JPA, garantizando integridad y consistencia de los datos.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Utiliza objetos de transferencia de datos (DTOs) para separar la capa de persistencia de la de presentación.</li>
 *   <li>Soporta paginación para optimizar el rendimiento en listados extensos.</li>
 *   <li>Integra funcionalidades de métricas como el cálculo del promedio de calificaciones por negocio.</li>
 *   <li>Gestiona el agregado de fotos con control de orden y validaciones de existencia.</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Registrar nuevas reseñas de usuarios (posts).</li>
 *   <li>Obtener una reseña individual o listados paginados por negocio o usuario.</li>
 *   <li>Agregar y administrar fotos asociadas a una reseña.</li>
 *   <li>Calcular métricas de rating y generar la respuesta de calificación del negocio.</li>
 *   <li>Eliminar reseñas cuando sea necesario (por ejemplo, moderación o eliminación de cuenta).</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear una reseña
 * CreatePostRequest req = new CreatePostRequest();
 * req.setAuthorUserId(2L);
 * req.setReviewedBusinessId(5L);
 * req.setRating((short)5);
 * req.setDescription("Excelente servicio y sabor.");
 * PostResponse nueva = postService.createPost(req);
 *
 * // Obtener reseña individual
 * PostResponse post = postService.getPost(12L);
 *
 * // Listar reseñas de un negocio (paginado)
 * Page&lt;PostResponse&gt; reviews = postService.listBusinessPosts(5L, 0, 10);
 *
 * // Agregar fotos a una reseña
 * AddPhotosRequest fotos = new AddPhotosRequest();
 * fotos.setUrls(List.of("https://cdn.mexazon.com/p1.jpg", "https://cdn.mexazon.com/p2.jpg"));
 * List&lt;PostResponse.Photo&gt; nuevasFotos = postService.addPhotos(12L, fotos);
 *
 * // Obtener rating promedio de un negocio
 * BusinessRatingResponse rating = postService.getBusinessRating(5L);
 * </pre>
 */
public interface PostService {

    /**
     * Crea una nueva reseña de usuario hacia un negocio.
     *
     * @param req objeto {@link CreatePostRequest} con los datos de la reseña.
     * @return la reseña creada como {@link PostResponse}.
     */
    PostResponse createPost(CreatePostRequest req);

    /**
     * Obtiene los detalles de una reseña específica.
     *
     * @param postId identificador único de la reseña.
     * @return representación de la reseña como {@link PostResponse}.
     */
    PostResponse getPost(Long postId);

    /**
     * Lista las reseñas asociadas a un negocio, ordenadas por fecha de creación descendente.
     * <p>Los resultados se devuelven paginados.</p>
     *
     * @param businessId identificador del negocio.
     * @param page número de página (base 0).
     * @param size tamaño de página.
     * @return página de reseñas como {@link Page}&lt;{@link PostResponse}&gt;.
     */
    Page<PostResponse> listBusinessPosts(Long businessId, int page, int size);

    /**
     * Lista las reseñas publicadas por un usuario.
     * <p>Los resultados se devuelven ordenados de la más reciente a la más antigua.</p>
     *
     * @param authorUserId identificador del autor.
     * @param page número de página.
     * @param size tamaño de página.
     * @return página de reseñas del usuario.
     */
    Page<PostResponse> listUserPosts(Long authorUserId, int page, int size);

    /**
     * Devuelve el “feed” general de reseñas más recientes en la plataforma.
     *
     * @param page número de página.
     * @param size tamaño de página.
     * @return página de reseñas ordenadas cronológicamente.
     */
    Page<PostResponse> feed(int page, int size);

    /**
     * Agrega fotografías a una reseña existente.
     *
     * @param postId identificador de la reseña.
     * @param req objeto {@link AddPhotosRequest} con la lista de URLs a agregar.
     * @return lista de fotos añadidas, representadas como {@link PostResponse.Photo}.
     */
    java.util.List<PostResponse.Photo> addPhotos(Long postId, AddPhotosRequest req);

    /**
     * Elimina una reseña y sus fotos asociadas.
     *
     * @param postId identificador de la reseña a eliminar.
     */
    void deletePost(Long postId);

    /**
     * Calcula y devuelve la información de calificación de un negocio.
     * <p>
     * Incluye el promedio, el número total de reseñas y la distribución de ratings.
     * </p>
     *
     * @param businessId identificador del negocio.
     * @return respuesta {@link BusinessRatingResponse} con las métricas calculadas.
     */
    BusinessRatingResponse getBusinessRating(Long businessId);
}