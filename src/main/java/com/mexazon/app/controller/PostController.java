package com.mexazon.app.controller;

import com.mexazon.app.model.Post;
import com.mexazon.app.service.impl.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la gestión de publicaciones/reseñas ({@link Post}).
 *
 * Base path: {@code /api}
 *
 * Endpoints incluidos:
 * <ul>
 *   <li>Listar posts por negocio (paginado).</li>
 *   <li>Listar posts por autor (paginado).</li>
 *   <li>Crear un post con IDs crudos.</li>
 *   <li>Actualizar rating/description de un post.</li>
 *   <li>Eliminar post.</li>
 * </ul>
 *
 * Notas:
 * - Paginación soportada vía Spring Data: {@code page}, {@code size}, {@code sort}.
 * - El servicio valida rating (1..5), unicidad autor-negocio y estado activo del negocio.
 */
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // ---------- READ ----------
    /**
     * Lista publicaciones asociadas a un negocio (paginado).
     *
     * Método/URL: GET /api/businesses/{businessId}/posts
     *
     * Path params:
     * - {@code businessId}: ID del negocio reseñado
     *
     * Query params:
     * - {@code page}, {@code size}, {@code sort} (por defecto: {@code createdAt,desc})
     *
     * Responses:
     * - 200 OK: página de publicaciones
     * - 404 Not Found: negocio no encontrado (si aplica en capa servicio/repositorio)
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/businesses/10/posts?page=0&size=20&sort=createdAt,desc"
     * </pre>
     */
    @GetMapping("/businesses/{businessId}/posts")
    public Page<Post> byBusiness(@PathVariable Long businessId,
                                 @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
                                 Pageable pageable) {
        return service.getByReviewedBusiness(businessId, pageable);
    }

    /**
     * Lista publicaciones realizadas por un autor (paginado).
     *
     * Método/URL: GET /api/users/{userId}/posts
     *
     * Path params:
     * - {@code userId}: ID del usuario autor de las publicaciones
     *
     * Query params:
     * - {@code page}, {@code size}, {@code sort} (por defecto: {@code createdAt,desc})
     *
     * Responses:
     * - 200 OK: página de publicaciones
     * - 404 Not Found: usuario no encontrado (si aplica en capa servicio/repositorio)
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/users/5/posts?page=0&size=20&sort=createdAt,desc"
     * </pre>
     */
    @GetMapping("/users/{userId}/posts")
    public Page<Post> byAuthor(@PathVariable Long userId,
                               @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
                               Pageable pageable) {
        return service.getByAuthor(userId, pageable);
    }

    // ---------- CREATE ----------
    /**
     * Crea una publicación con IDs crudos (autor y negocio).
     * <p>
     * El servicio aplicará validaciones de:
     * <ul>
     *   <li>Autor y negocio existentes.</li>
     *   <li>Rating en el rango 1..5.</li>
     *   <li>Negocio activo.</li>
     *   <li>Unicidad: un autor no puede reseñar el mismo negocio dos veces.</li>
     * </ul>
     *
     * Método/URL: POST /api/posts
     *
     * Body:
     * <pre>
     * {
     *   "authorUserId": 5,          // Long (obligatorio)
     *   "reviewedBusinessId": 12,   // Long (obligatorio)
     *   "rating": 4,                // int 1..5 (obligatorio)
     *   "description": "Muy buen servicio" // opcional
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: post creado
     * - 400 Bad Request: validaciones (rating, unicidad, negocio inactivo)
     * - 404 Not Found: autor o negocio inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/posts" \
     *   -H "Content-Type: application/json" \
     *   -d '{"authorUserId":5,"reviewedBusinessId":12,"rating":4,"description":"Muy buen servicio"}'
     * </pre>
     */
    @PostMapping("/posts")
    public Post create(@RequestBody Map<String, Object> body) {
        Long authorId   = Long.valueOf(body.get("authorUserId").toString());
        Long businessId = Long.valueOf(body.get("reviewedBusinessId").toString());
        int rating      = Integer.parseInt(body.get("rating").toString());
        String description = (String) body.get("description");
        return service.createByIds(authorId, businessId, rating, description);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza el rating y/o la descripción de una publicación.
     * <p>
     * Reglas:
     * <ul>
     *   <li>{@code rating} es obligatorio para actualizar y debe estar en 1..5.</li>
     *   <li>{@code description} es opcional.</li>
     * </ul>
     *
     * Método/URL: PUT /api/posts/{postId}
     *
     * Path params:
     * - {@code postId}: ID del post a actualizar
     *
     * Body:
     * <pre>
     * {
     *   "rating": 5,               // obligatorio (1..5)
     *   "description": "Excelente" // opcional
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: publicación actualizada
     * - 400 Bad Request: rating faltante o fuera de rango
     * - 404 Not Found: post no encontrado
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT "http://localhost:8080/api/posts/100" \
     *   -H "Content-Type: application/json" \
     *   -d '{"rating":5,"description":"Excelente"}'
     * </pre>
     */
    @PutMapping("/posts/{postId}")
    public Post update(@PathVariable Long postId, @RequestBody Map<String, Object> body) {
        Integer rating        = body.get("rating") == null ? null : Integer.valueOf(body.get("rating").toString());
        String description    = (String) body.get("description");
        if (rating == null) throw new IllegalArgumentException("rating es obligatorio para actualizar");
        return service.update(postId, rating, description);
    }

    // ---------- DELETE ----------
    /**
     * Elimina una publicación por su identificador.
     *
     * Método/URL: DELETE /api/posts/{postId}
     *
     * Path params:
     * - {@code postId}: ID del post a eliminar
     *
     * Responses:
     * - 200 OK / 204 No Content: eliminado (según configuración)
     * - 404 Not Found: post inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/posts/100"
     * </pre>
     */
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        service.delete(postId);
    }
}