package com.mexazon.app.controller;

import com.mexazon.app.dto.*;
import com.mexazon.app.service.PostService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de reseñas (posts) y sus fotografías.
 * <p>
 * Expone endpoints para crear, consultar, listar, eliminar reseñas,
 * agregar fotos a una reseña existente y consultar el rating de un negocio.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Crear reseñas con validación de rating y unicidad (usuario-negocio).</li>
 *   <li>Obtener reseñas individuales y listados paginados (por negocio, por usuario, feed).</li>
 *   <li>Agregar fotos con verificación de colisiones en el <i>photoOrder</i>.</li>
 *   <li>Eliminar reseñas (las fotos se eliminan por cascada).</li>
 *   <li>Proveer métricas de calificación (promedio, conteo y distribución).</li>
 * </ul>
 *
 * <h3>Manejo de errores:</h3>
 * <ul>
 *   <li><b>400 Bad Request</b>: datos inválidos (p. ej., rating fuera de rango).</li>
 *   <li><b>404 Not Found</b>: reseña o recurso inexistente.</li>
 *   <li><b>409 Conflict</b>: violaciones de integridad (reseña duplicada o orden de foto repetido).</li>
 * </ul>
 *
 * <h3>Ejemplos rápidos:</h3>
 * <pre>
 * POST /api/posts
 * { "authorUserId":2, "reviewedBusinessId":5, "rating":5, "description":"Excelente", "photos":[{"photoUrl":"..."}] }
 *
 * GET /api/posts/12
 * GET /api/businesses/5/posts?page=0&size=10
 * GET /api/users/2/posts?page=0&size=10
 * GET /api/feed/posts?page=0&size=10
 * POST /api/posts/12/photos
 * { "photos":[{"photoUrl":"...", "photoOrder":1}] }
 *
 * GET /api/businesses/5/rating
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class PostController {

    /** Capa de servicio que encapsula la lógica de reseñas y fotos. */
    private final PostService service;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param service servicio de reseñas
     */
    public PostController(PostService service) {
        this.service = service;
    }

    // ---------- POST /api/posts ----------
    /**
     * Crea una nueva reseña de usuario hacia un negocio.
     * <p>
     * Valida que el <code>rating</code> esté entre 1 y 5 y que
     * no exista una reseña previa del mismo usuario para el mismo negocio.
     * </p>
     *
     * @param req DTO {@link CreatePostRequest} con datos de la reseña (y fotos opcionales).
     * @return {@code 201 Created} con {@link PostResponse}; {@code 400} o {@code 409} según error.
     */
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createPost(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("status", 400, "error", "Bad Request", "message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    java.util.Map.of("status", 409, "error", "Conflict", "message", e.getMessage()));
        }
    }

    // ---------- GET /api/posts/{postId} ----------
    /**
     * Recupera una reseña por su identificador.
     *
     * @param postId id de la reseña
     * @return {@code 200 OK} con {@link PostResponse} o {@code 404 Not Found}.
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        try {
            return ResponseEntity.ok(service.getPost(postId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("status", 404, "error", "Not Found", "message", e.getMessage()));
        }
    }

    // ---------- POST /api/posts/{postId}/photos ----------
    /**
     * Agrega fotografías a una reseña existente, respetando/validando el orden de las fotos.
     *
     * @param postId id de la reseña
     * @param req DTO {@link AddPhotosRequest} con la lista de fotos a anexar
     * @return {@code 201 Created} con lista de {@link PostResponse.Photo}; {@code 404} o {@code 409} según error.
     */
    @PostMapping("/posts/{postId}/photos")
    public ResponseEntity<?> addPhotos(@PathVariable("postId") Long postId, @RequestBody AddPhotosRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addPhotos(postId, req));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("status", 404, "error", "Not Found", "message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    java.util.Map.of("status", 409, "error", "Conflict", "message", e.getMessage()));
        }
    }

    // ---------- DELETE /api/posts/{postId} ----------
    /**
     * Elimina una reseña (sus fotos se eliminan por cascada).
     *
     * @param postId id de la reseña
     * @return {@code 204 No Content} si se eliminó; {@code 404 Not Found} si no existe.
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("") Long postId) {
        try {
            service.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("status", 404, "error", "Not Found", "message", e.getMessage()));
        }
    }

    // ---------- GET /api/businesses/{businessId}/posts ----------
    /**
     * Lista paginada de reseñas de un negocio, ordenadas por fecha (desc).
     *
     * @param businessId id del negocio
     * @param page página (base 0)
     * @param size tamaño de página
     * @return {@link Page} de {@link PostResponse} con {@code 200 OK}
     */
    @GetMapping("/businesses/{businessId}/posts")
    public ResponseEntity<Page<PostResponse>> listBusinessPosts(
            @PathVariable("businessId") Long businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listBusinessPosts(businessId, page, size));
    }

    // ---------- GET /api/users/{authorUserId}/posts ----------
    /**
     * Lista paginada de reseñas creadas por un usuario.
     *
     * @param authorUserId id del autor
     * @param page página (base 0)
     * @param size tamaño de página
     * @return {@link Page} de {@link PostResponse} con {@code 200 OK}
     */
    @GetMapping("/users/{authorUserId}/posts")
    public ResponseEntity<Page<PostResponse>> listUserPosts(
            @PathVariable("authorUserId") Long authorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listUserPosts(authorUserId, page, size));
    }

    // ---------- GET /api/feed/posts ----------
    /**
     * Feed global de reseñas más recientes (paginado).
     *
     * @param page página (base 0)
     * @param size tamaño de página
     * @return {@link Page} de {@link PostResponse} con {@code 200 OK}
     */
    @GetMapping("/feed/posts")
    public ResponseEntity<Page<PostResponse>> feed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.feed(page, size));
    }

    // ---------- GET /api/businesses/{businessId}/rating ----------
    /**
     * Devuelve el rating agregado de un negocio:
     * promedio, número total de reseñas y distribución por estrellas.
     *
     * @param businessId id del negocio
     * @return {@link BusinessRatingResponse} con {@code 200 OK}
     */
    @GetMapping("/businesses/{businessId}/rating")
    public ResponseEntity<BusinessRatingResponse> rating(@PathVariable("businessId") Long businessId) {
        return ResponseEntity.ok(service.getBusinessRating(businessId));
    }
}