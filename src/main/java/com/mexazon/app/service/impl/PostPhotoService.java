package com.mexazon.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mexazon.app.model.Post;
import com.mexazon.app.model.PostPhoto;
import com.mexazon.app.repository.PostPhotoRepository;

/**
 * Servicio para gestionar las fotos de los posts.
 * Proporciona operaciones CRUD para las fotos asociadas a las reseñas de negocios.
 */
@Service
@Transactional
public class PostPhotoService {

    private final PostPhotoRepository repository;

    /**
     * Constructor del servicio de fotos de posts.
     *
     * @param repository Repositorio de fotos de posts
     */
    public PostPhotoService(PostPhotoRepository repository) {
        this.repository = repository;
    }

    // ---------- CREATE ----------
    /**
     * Crea una nueva foto asociada a un post.
     * Valida que la URL no esté vacía y que no exista otra foto
     * con el mismo orden para el mismo post.
     *
     * @param post Post al que pertenece la foto
     * @param photoUrl URL de la foto
     * @param photoOrder Orden de la foto en el post (puede ser null)
     * @return La foto creada
     * @throws IllegalArgumentException si la URL está vacía
     * @throws IllegalArgumentException si ya existe una foto con ese orden para el post
     */
    public PostPhoto create(Post post, String photoUrl, Short photoOrder) {
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la foto no puede estar vacía");
        }

        // Validar que no exista otra foto con el mismo orden para este post
        if (photoOrder != null) {
            PostPhoto existing = repository.findByPostId_PostIdAndPhotoOrder(post.getPostId(), photoOrder);
            if (existing != null) {
                throw new IllegalArgumentException("Ya existe una foto con ese orden para este post");
            }
        }

        PostPhoto photo = new PostPhoto();
        photo.setPhotoUrl(photoUrl);
        photo.setPhotoOrder(photoOrder);

        return repository.save(photo);
    }

    // ---------- READ ----------
    /**
     * Obtiene una foto por su ID.
     *
     * @param photoId ID de la foto
     * @return La foto encontrada
     * @throws IllegalArgumentException si no se encuentra la foto
     */
    @Transactional(readOnly = true)
    public PostPhoto get(Long photoId) {
        return repository.findById(photoId)
            .orElseThrow(() -> new IllegalArgumentException("Foto no encontrada"));
    }

    /**
     * Obtiene todas las fotos asociadas a un post específico.
     *
     * @param postId ID del post
     * @return Lista de fotos del post
     */
    @Transactional(readOnly = true)
    public List<PostPhoto> getAllByPost(Long postId) {
        return repository.findByPostId_PostId(postId);
    }

    /**
     * Obtiene una foto específica por post y orden.
     *
     * @param postId ID del post
     * @param photoOrder Orden de la foto
     * @return La foto encontrada
     * @throws IllegalArgumentException si no se encuentra la foto
     */
    @Transactional(readOnly = true)
    public PostPhoto getByPostAndOrder(Long postId, Short photoOrder) {
        PostPhoto photo = repository.findByPostId_PostIdAndPhotoOrder(postId, photoOrder);
        if (photo == null) {
            throw new IllegalArgumentException("Foto no encontrada para ese post y orden");
        }
        return photo;
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza una foto existente.
     * Permite actualizar la URL y/o el orden de la foto.
     * Valida que no haya conflictos con el orden de otras fotos del mismo post.
     *
     * @param photoId ID de la foto a actualizar
     * @param photoUrl Nueva URL (null para mantener la actual)
     * @param photoOrder Nuevo orden (null para mantener el actual)
     * @return La foto actualizada
     * @throws IllegalArgumentException si no se encuentra la foto
     * @throws IllegalArgumentException si el nuevo orden ya está en uso por otra foto
     */
    public PostPhoto update(Long photoId, String photoUrl, Short photoOrder) {
        PostPhoto existing = get(photoId);

        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            existing.setPhotoUrl(photoUrl);
        }

        if (photoOrder != null) {
            // Validar que no haya conflicto con otra foto del mismo post
           PostPhoto conflict = repository.findByPostId_PostIdAndPhotoOrder(
    existing.getPostId().getPostId(), photoOrder);
            if (conflict != null && !conflict.equals(existing)) {
                throw new IllegalArgumentException("Ya existe otra foto con ese orden para este post");
            }
            existing.setPhotoOrder(photoOrder);
        }

        return repository.save(existing);
    }

    // ---------- DELETE ----------
    /**
     * Elimina una foto por su ID.
     *
     * @param photoId ID de la foto a eliminar
     * @throws IllegalArgumentException si no se encuentra la foto
     */
    public void delete(Long photoId) {
        if (!repository.existsById(photoId)) {
            throw new IllegalArgumentException("Foto no encontrada");
        }
        repository.deleteById(photoId);
    }

    /**
     * Elimina todas las fotos asociadas a un post.
     *
     * @param postId ID del post cuyas fotos se eliminarán
     */
    public void deleteAllByPost(Long postId) {
        List<PostPhoto> photos = repository.findByPostId_PostId(postId);
        repository.deleteAll(photos);
    }
}
