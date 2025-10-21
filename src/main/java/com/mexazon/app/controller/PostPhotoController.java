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
 * Controlador REST para gestionar las fotos de los posts.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar fotos.
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

    /**
     * Crea una nueva foto asociada a un post.
     * POST /api/post-photos
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

    /**
     * Obtiene una foto por su ID.
     * GET /api/post-photos/{photoId}
     */
    @GetMapping("/{photoId}")
    public ResponseEntity<PostPhoto> get(@PathVariable Long photoId) {
        PostPhoto photo = postPhotoService.get(photoId);
        return ResponseEntity.ok(photo);
    }

    /**
     * Obtiene todas las fotos de un post específico.
     * GET /api/post-photos/post/{postId}
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostPhoto>> getAllByPost(@PathVariable Long postId) {
        List<PostPhoto> photos = postPhotoService.getAllByPost(postId);
        return ResponseEntity.ok(photos);
    }

    /**
     * Obtiene una foto específica por post y orden.
     * GET /api/post-photos/post/{postId}/order/{photoOrder}
     */
    @GetMapping("/post/{postId}/order/{photoOrder}")
    public ResponseEntity<PostPhoto> getByPostAndOrder(@PathVariable Long postId,
                                                       @PathVariable Short photoOrder) {
        PostPhoto photo = postPhotoService.getByPostAndOrder(postId, photoOrder);
        return ResponseEntity.ok(photo);
    }

    /**
     * Actualiza una foto existente.
     * PUT /api/post-photos/{photoId}
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

    /**
     * Elimina una foto por su ID.
     * DELETE /api/post-photos/{photoId}
     */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable Long photoId) {
        postPhotoService.delete(photoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina todas las fotos de un post.
     * DELETE /api/post-photos/post/{postId}
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteAllByPost(@PathVariable Long postId) {
        postPhotoService.deleteAllByPost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clase interna para la solicitud de creación de foto.
     */
    public static class CreatePostPhotoRequest {
        public Long postId;
        public String photoUrl;
        public Short photoOrder;
    }

    /**
     * Clase interna para la solicitud de actualización de foto.
     */
    public static class UpdatePostPhotoRequest {
        public String photoUrl;
        public Short photoOrder;
    }
}