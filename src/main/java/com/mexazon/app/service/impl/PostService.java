package com.mexazon.app.service.impl;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Post;
import com.mexazon.app.model.User;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.PostRepository;
import com.mexazon.app.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de publicaciones y reseñas ({@link Post}).
 * <p>
 * Proporciona operaciones CRUD y validaciones relacionadas con las reseñas
 * que los usuarios realizan sobre los negocios dentro de la plataforma.
 * <br>
 * Incluye validaciones de unicidad, límites de calificación y estado activo del negocio.
 */
@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    /**
     * Constructor del servicio de publicaciones.
     *
     * @param postRepository Repositorio de publicaciones
     * @param userRepository Repositorio de usuarios
     * @param businessRepository Repositorio de negocios
     */
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       BusinessRepository businessRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    // ---------- CREATE ----------
    /**
     * Crea una nueva publicación o reseña.
     * <p>
     * Valida los siguientes puntos:
     * <ul>
     *   <li>El autor y el negocio reseñado son obligatorios.</li>
     *   <li>La calificación ({@code rating}) debe estar entre 1 y 5.</li>
     *   <li>El negocio debe estar activo.</li>
     *   <li>El autor no debe haber reseñado previamente el mismo negocio.</li>
     * </ul>
     *
     * @param post Objeto {@link Post} a crear
     * @return La publicación creada
     * @throws IllegalArgumentException si alguna validación falla
     */
    public Post create(Post post) {
        if (post.getAuthor() == null || post.getReviewedBusiness() == null) {
            throw new IllegalArgumentException("El autor y el negocio reseñado son obligatorios");
        }
        // Validación del rango de calificación
        if (post.getRating() < 1 || post.getRating() > 5) {
            throw new IllegalArgumentException("rating debe estar entre 1 y 5");
        }
        // Verifica que el negocio esté activo
        Business biz = post.getReviewedBusiness();
        if (!(biz.isActive())) { // Si el getter se llama getIsActive(), actualizar aquí
            throw new IllegalArgumentException("El negocio no está activo");
        }
        // Unicidad: un autor solo puede reseñar un negocio una vez
        if (postRepository.existsByAuthorAndReviewedBusiness(post.getAuthor(), biz)) {
            throw new IllegalArgumentException("El autor ya reseñó ese negocio");
        }
        return postRepository.save(post);
    }

    /**
     * Crea una publicación o reseña usando los identificadores del autor y del negocio.
     * <p>
     * Este método es útil para controladores que reciben IDs en lugar de objetos completos.
     * Internamente aplica las mismas reglas que {@link #create(Post)}.
     *
     * @param authorId ID del usuario autor de la reseña
     * @param businessId ID del negocio reseñado
     * @param rating Calificación (1–5)
     * @param description Descripción o texto de la reseña
     * @return La publicación creada
     * @throws IllegalArgumentException si el usuario o negocio no existen o se violan reglas de validación
     */
    public Post createByIds(Long authorId, Long businessId, int rating, String description) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario (autor) no encontrado"));
        Business biz = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));

        Post p = new Post();
        p.setAuthor(author);
        p.setReviewedBusiness(biz);
        p.setRating(rating);
        p.setDescription(description);

        return create(p); // Aplica las validaciones definidas en el método principal
    }

    // ---------- READ ----------
    /**
     * Obtiene todas las publicaciones registradas.
     *
     * @return Lista completa de publicaciones
     */
    @Transactional(readOnly = true)
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    /**
     * Obtiene una publicación específica por su identificador.
     *
     * @param id ID de la publicación
     * @return La publicación encontrada
     * @throws IllegalArgumentException si la publicación no existe
     */
    @Transactional(readOnly = true)
    public Post getById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado con id: " + id));
    }

    /**
     * Obtiene las publicaciones asociadas a un negocio determinado.
     *
     * @param businessId ID del negocio reseñado
     * @param pageable Parámetros de paginación
     * @return Página con las publicaciones encontradas
     * @throws IllegalArgumentException si el negocio no existe
     */
    @Transactional(readOnly = true)
    public Page<Post> getByReviewedBusiness(Long businessId, Pageable pageable) {
        Business biz = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
        return postRepository.findAllByReviewedBusiness(biz, pageable);
    }

    /**
     * Obtiene las publicaciones realizadas por un autor específico.
     * <p>
     * Si el repositorio no implementa el método paginado, se construye
     * manualmente una página con base en la lista completa.
     *
     * @param authorUserId ID del usuario autor
     * @param pageable Parámetros de paginación
     * @return Página con las publicaciones del autor
     * @throws IllegalArgumentException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public Page<Post> getByAuthor(Long authorUserId, Pageable pageable) {
        User author = userRepository.findById(authorUserId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Opción A (preferida): si el método existe en el repositorio
        try {
            return postRepository.findAllByAuthor(author, pageable);
        } catch (Throwable ignore) { /* fallback si el método no existe */ }

        // Opción B: construir la paginación manualmente
        List<Post> all = postRepository.findByAuthor(author);
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), all.size());
        if (start > end) return new PageImpl<>(List.of(), pageable, all.size());
        return new PageImpl<>(all.subList(start, end), pageable, all.size());
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza la calificación y/o descripción de una publicación.
     * <p>
     * La calificación debe estar en el rango 1–5.
     *
     * @param id ID de la publicación a actualizar
     * @param rating Nueva calificación (1–5)
     * @param description Nueva descripción (opcional)
     * @return La publicación actualizada
     * @throws IllegalArgumentException si la publicación no existe o el rating es inválido
     */
    public Post update(Long id, int rating, String description) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("rating debe estar entre 1 y 5");

        Post existing = getById(id);
        existing.setRating(rating);
        if (description != null) {
            existing.setDescription(description);
        }
        return postRepository.save(existing);
    }

    // ---------- DELETE ----------
    /**
     * Elimina una publicación por su identificador.
     *
     * @param id ID de la publicación a eliminar
     * @throws IllegalArgumentException si la publicación no existe
     */
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post no encontrado con id: " + id);
        }
        postRepository.deleteById(id);
    }
}