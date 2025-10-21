package com.mexazon.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mexazon.app.model.Post;
import com.mexazon.app.model.PostPhoto;
import com.mexazon.app.repository.PostRepository;
import com.mexazon.app.service.impl.PostPhotoService;

/**
 * Controlador REST para la gestión de fotos asociadas a publicaciones ({@link PostPhoto}).
 *
 * Base path: {@code /api/post-photos}
 *
 * Endpoints:
 * <ul>
 *   <li>Crear foto para un post.</li>
 *   <li>Obtener foto por ID.</li>
 *   <li>Listar fotos por post.</li>
 *   <li>Obtener foto específica por post y orden.</li>
 *   <li>Actualizar foto.</li>
 *   <li>Eliminar foto por ID o eliminar todas las fotos de un post.</li>
 * </ul>
 *
 * Notas:
 * - El orden ({@code photoOrder}) permite controlar la posición de la foto dentro del post.
 * - Las validaciones de negocio se aplican en la capa de servicio.
 */
@RestController
@RequestMapping("/api/post-photos")
public class PostPhotoController {

    private final PostPhotoService postPhotoService;
    private final PostRepository postRepository;

    public PostPhotoController(PostPhotoService postPhotoService,
                               PostRepository postRepository) {
        this.postPhotoService = postPhotoService;
        this.postRepository = postRepository;
    }

    // ---------- CREATE ----------
    /**
     * Crea una nueva foto asociada a un post.
     *
     * Método/URL: POST /api/post-photos
     *
     * Body:
     * <pre>
     * {
     *   "postId": 100,             // ID del post (obligatorio)
     *   "photoUrl": "https://...", // URL de la imagen (obligatorio)
     *   "photoOrder": 1            // Orden dentro del post (opcional)
     * }
     * </pre>
     *
     * Responses:
     * - 201 Created: foto creada
     * - 400 Bad Request: validaciones fallidas (p. ej. datos obligatorios)
     * - 404 Not Found: post inexistente
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/post-photos" \
     *   -H "Content-Type: application/json" \
     *   -d '{"postId":100,"photoUrl":"https://cdn/img.jpg","photoOrder":1}'
     * </pre>
     */
    @PostMapping
    public ResponseEntity<PostPhoto> create(@RequestBody CreatePostPhotoRequest request) {
        Post post = postRepository.findById(request.postId)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado"));

        PostPhoto created = postPhotoService.create(
            post,
            request.photoUrl,
            request.photoOrder
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- READ ----------
    /**
     * Obtiene una foto por su ID.
     *
     * Método/URL: GET /api/post-photos/{photoId}
     *
     * Path params:
     * - {@code photoId}: ID de la foto
     *
     * Responses:
     * - 200 OK: foto encontrada
     * - 404 Not Found: foto no existente
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/post-photos/25"
     * </pre>
     */
    @GetMapping("/{photoId}")
    public ResponseEntity<PostPhoto> get(@PathVariable Long photoId) {
        PostPhoto photo = postPhotoService.get(photoId);
        return ResponseEntity.ok(photo);
    }

    /**
     * Lista todas las fotos de un post específico.
     *
     * Método/URL: GET /api/post-photos/post/{postId}
     *
     * Path params:
     * - {@code postId}: ID del post
     *
     * Responses:
     * - 200 OK: lista de fotos
     * - 404 Not Found: post inexistente (si aplica en la capa de servicio)
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/post-photos/post/100"
     * </pre>
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostPhoto>> getAllByPost(@PathVariable Long postId) {
        List<PostPhoto> photos = postPhotoService.getAllByPost(postId);
        return ResponseEntity.ok(photos);
    }

    /**
     * Obtiene una foto específica por post y orden.
     *
     * Método/URL: GET /api/post-photos/post/{postId}/order/{photoOrder}
     *
     * Path params:
     * - {@code postId}: ID del post
     * - {@code photoOrder}: orden de la foto dentro del post
     *
     * Responses:
     * - 200 OK: foto encontrada
     * - 404 Not Found: no existe foto con ese orden para el post
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/post-photos/post/100/order/1"
     * </pre>
     */
    @GetMapping("/post/{postId}/order/{photoOrder}")
    public ResponseEntity<PostPhoto> getByPostAndOrder(@PathVariable Long postId,
                                                       @PathVariable Short photoOrder) {
        PostPhoto photo = postPhotoService.getByPostAndOrder(postId, photoOrder);
        return ResponseEntity.ok(photo);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza una foto existente.
     *
     * Método/URL: PUT /api/post-photos/{photoId}
     *
     * Path params:
     * - {@code photoId}: ID de la foto a actualizar
     *
     * Body (campos opcionales; null conserva el valor actual):
     * <pre>
     * {
     *   "photoUrl": "https://...", // nueva URL (opcional)
     *   "photoOrder": 2            // nuevo orden (opcional)
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: foto actualizada
     * - 404 Not Found: foto no existente
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT "http://localhost:8080/api/post-photos/25" \
     *   -H "Content-Type: application/json" \
     *   -d '{"photoUrl":"https://cdn/new.jpg","photoOrder":2}'
     * </pre>
     */
    @PutMapping("/{photoId}")
    public ResponseEntity<PostPhoto> update(@PathVariable Long photoId,
                                           @RequestBody UpdatePostPhotoRequest request) {
        PostPhoto updated = postPhotoService.update(
            photoId,
            request.photoUrl,
            request.photoOrder
        );

        return ResponseEntity.ok(updated);
    }

    // ---------- DELETE ----------
    /**
     * Elimina una foto por su ID.
     *
     * Método/URL: DELETE /api/post-photos/{photoId}
     *
     * Path params:
     * - {@code photoId}: ID de la foto a eliminar
     *
     * Responses:
     * - 204 No Content: eliminado con éxito
     * - 404 Not Found: foto no existente
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/post-photos/25"
     * </pre>
     */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable Long photoId) {
        postPhotoService.delete(photoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina todas las fotos de un post.
     *
     * Método/URL: DELETE /api/post-photos/post/{postId}
     *
     * Path params:
     * - {@code postId}: ID del post
     *
     * Responses:
     * - 204 No Content: fotos eliminadas
     * - 404 Not Found: post inexistente (si aplica en la capa de servicio)
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/post-photos/post/100"
     * </pre>
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteAllByPost(@PathVariable Long postId) {
        postPhotoService.deleteAllByPost(postId);
        return ResponseEntity.noContent().build();
    }

    // ========= DTOs de request =========

    /**
     * Payload para crear una foto asociada a un post.
     */
    public static class CreatePostPhotoRequest {
        /** ID del post propietario. */
        public Long postId;
        /** URL de la imagen. */
        public String photoUrl;
        /** Orden de la foto dentro del post. */
        public Short photoOrder;
    }

    /**
     * Payload para actualizar una foto (parcial).
     * Campos en null conservan su valor previo.
     */
    public static class UpdatePostPhotoRequest {
        /** Nueva URL de la imagen (opcional). */
        public String photoUrl;
        /** Nuevo orden de la foto (opcional). */
        public Short photoOrder;
    }
}