package com.mexazon.app.service.impl;

import com.mexazon.app.dto.*;
import com.mexazon.app.model.Post;
import com.mexazon.app.model.PostPhoto;
import com.mexazon.app.repository.PostPhotoRepository;
import com.mexazon.app.repository.PostRepository;
import com.mexazon.app.service.PostService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio {@link PostService}.
 * <p>
 * Gestiona la lógica de negocio para reseñas (posts) y sus fotografías:
 * validación de rating, unicidad de reseña por usuario/negocio, orden de fotos,
 * mapeo a DTOs y obtención de métricas de calificación.
 * </p>
 *
 * <h3>Responsabilidades clave</h3>
 * <ul>
 *   <li>Crear reseñas con validaciones de negocio y soporte para fotos iniciales.</li>
 *   <li>Obtener reseñas individuales y listados paginados (por negocio, usuario o feed).</li>
 *   <li>Agregar fotos con control estricto del orden y verificación de duplicados.</li>
 *   <li>Eliminar reseñas (las fotos asociadas se eliminan por cascada).</li>
 *   <li>Calcular métricas de rating: promedio, conteo y distribución por estrellas.</li>
 * </ul>
 *
 * <h3>Diseño e implementación</h3>
 * <ul>
 *   <li>Uso de DTOs ({@link CreatePostRequest}, {@link PostResponse}, {@link AddPhotosRequest},
 *       {@link BusinessRatingResponse}) para desacoplar persistencia de la capa de presentación.</li>
 *   <li>Transaccionalidad con {@link Transactional}:
 *     <ul>
 *       <li><b>write</b>: métodos que mutan estado usan transacciones por defecto.</li>
 *       <li><b>read</b>: consultas usan {@code readOnly = true} para optimizar.</li>
 *     </ul>
 *   </li>
 *   <li>Validaciones:
 *     <ul>
 *       <li>Rating en rango [1..5].</li>
 *       <li>Una reseña por combinación usuario/negocio (chequeo previo).</li>
 *       <li>Orden de fotos único por post (al agregar fotos).</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>Excepciones</h3>
 * <ul>
 *   <li>{@link IllegalArgumentException}: rating inválido.</li>
 *   <li>{@link DataIntegrityViolationException}: reseña duplicada por usuario/negocio,
 *       o conflicto de orden de foto.</li>
 *   <li>{@link NoSuchElementException}: post inexistente o al consultar entrada no encontrada.</li>
 * </ul>
 */
@Service
public class PostServiceImpl implements PostService {

    /** Repositorio principal de reseñas. */
    private final PostRepository postRepo;

    /** Repositorio de fotografías asociadas a reseñas. */
    private final PostPhotoRepository photoRepo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param postRepo  repositorio de posts
     * @param photoRepo repositorio de fotos de post
     */
    public PostServiceImpl(PostRepository postRepo, PostPhotoRepository photoRepo) {
        this.postRepo = postRepo;
        this.photoRepo = photoRepo;
    }

    // =========================
    // Helpers de mapeo DTO
    // =========================

    /**
     * Convierte una entidad {@link Post} a {@link PostResponse}, incluyendo fotos ordenadas.
     */
    private PostResponse toResponse(Post p) {
        PostResponse r = new PostResponse();
        r.postId = p.getPostId();
        r.authorUserId = p.getAuthorUserId();
        r.reviewedBusinessId = p.getReviewedBusinessId();
        r.rating = p.getRating();
        r.description = p.getDescription();
        r.createdAt = p.getCreatedAt();
        r.photos = p.getPhotos().stream().map(ph -> {
            PostResponse.Photo x = new PostResponse.Photo();
            x.photoId = ph.getPhotoId();
            x.photoUrl = ph.getPhotoUrl();
            x.photoOrder = ph.getPhotoOrder();
            return x;
        }).sorted(Comparator.comparingInt(a -> a.photoOrder)).toList();
        return r;
    }

    /**
     * Convierte una página de {@link Post} en una página de {@link PostResponse}.
     */
    private Page<PostResponse> toResponsePage(Page<Post> page) {
        List<PostResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    // =========================
    // Validaciones
    // =========================

    /**
     * Valida que el rating esté en el rango [1..5].
     *
     * @param rating valor a validar
     * @throws IllegalArgumentException si el rating es nulo o fuera de rango
     */
    private void validateRating(Short rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
    }

    // =========================
    // Implementación de PostService
    // =========================

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest req) {
        validateRating(req.rating);

        // Evitar duplicado: un usuario solo puede reseñar un negocio una vez
        if (postRepo.existsByAuthorUserIdAndReviewedBusinessId(req.authorUserId, req.reviewedBusinessId)) {
            throw new DataIntegrityViolationException("User can only review a business once");
        }

        // Construir entidad Post
        Post p = new Post();
        p.setAuthorUserId(req.authorUserId);
        p.setReviewedBusinessId(req.reviewedBusinessId);
        p.setRating(req.rating);
        p.setDescription(req.description);

        // Fotos iniciales (opcional) — se respetan órdenes provistos; si falta, se autoincrementa
        if (req.photos != null && !req.photos.isEmpty()) {
            int next = 1;
            for (CreatePostRequest.PhotoItem it : req.photos) {
                PostPhoto ph = new PostPhoto();
                ph.setPost(p); // cascade ALL en Post.persistirá la foto
                ph.setPhotoUrl(it.photoUrl);
                ph.setPhotoOrder(it.photoOrder != null ? it.photoOrder : next++);
                p.getPhotos().add(ph);
            }
        }

        Post saved = postRepo.save(p);
        return toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post p = postRepo.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));

        // Forzar orden de fotos en memoria por seguridad
        p.setPhotos(p.getPhotos().stream()
                .sorted(Comparator.comparing(PostPhoto::getPhotoOrder))
                .collect(Collectors.toList()));

        return toResponse(p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listBusinessPosts(Long businessId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toResponsePage(postRepo.findByReviewedBusinessIdOrderByCreatedAtDesc(businessId, pageable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listUserPosts(Long authorUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toResponsePage(postRepo.findByAuthorUserIdOrderByCreatedAtDesc(authorUserId, pageable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> feed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toResponsePage(postRepo.findAllByOrderByCreatedAtDesc(pageable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<PostResponse.Photo> addPhotos(Long postId, AddPhotosRequest req) {
        Post p = postRepo.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));

        // Obtener último orden para asignar siguientes automáticamente
        int nextOrder = photoRepo.findByPost_PostIdOrderByPhotoOrderAsc(postId)
                .stream().mapToInt(PostPhoto::getPhotoOrder).max().orElse(0) + 1;

        List<PostResponse.Photo> result = new ArrayList<>();
        for (AddPhotosRequest.PhotoItem it : req.photos) {
            Integer order = (it.photoOrder != null) ? it.photoOrder : nextOrder++;
            if (photoRepo.existsByPost_PostIdAndPhotoOrder(postId, order)) {
                throw new DataIntegrityViolationException("Photo order already exists for this post");
            }
            PostPhoto ph = new PostPhoto();
            ph.setPost(p);
            ph.setPhotoUrl(it.photoUrl);
            ph.setPhotoOrder(order);
            PostPhoto saved = photoRepo.save(ph);

            PostResponse.Photo dto = new PostResponse.Photo();
            dto.photoId = saved.getPhotoId();
            dto.photoUrl = saved.getPhotoUrl();
            dto.photoOrder = saved.getPhotoOrder();
            result.add(dto);
        }
        return result.stream().sorted(Comparator.comparingInt(a -> a.photoOrder)).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deletePost(Long postId) {
        if (!postRepo.existsById(postId)) {
            throw new NoSuchElementException("Post not found");
        }
        // Las fotos se eliminan por cascada (FK/ORM)
        postRepo.deleteById(postId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public BusinessRatingResponse getBusinessRating(Long businessId) {
        Double avg = postRepo.avgRatingByBusiness(businessId);
        Long count = postRepo.countByBusiness(businessId);
        List<Object[]> rows = postRepo.distributionByBusiness(businessId);

        Map<Integer, Long> dist = new HashMap<>();
        for (int i = 1; i <= 5; i++) dist.put(i, 0L);
        for (Object[] r : rows) {
            Integer rating = ((Number) r[0]).intValue();
            Long cnt = ((Number) r[1]).longValue();
            dist.put(rating, cnt);
        }

        BusinessRatingResponse res = new BusinessRatingResponse();
        res.businessId = businessId;
        res.reviewsCount = (count == null ? 0L : count);
        res.averageRating = BigDecimal.valueOf(avg == null ? 0.0 : avg)
                .setScale(2, RoundingMode.HALF_UP);
        res.distribution = dist;
        return res;
    }
}