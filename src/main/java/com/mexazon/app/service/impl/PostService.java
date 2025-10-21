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

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       BusinessRepository businessRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    // ---------- CREATE ----------
    public Post create(Post post) {
        if (post.getAuthor() == null || post.getReviewedBusiness() == null) {
            throw new IllegalArgumentException("El autor y el negocio reseñado son obligatorios");
        }
        // rating 1..5
        if (post.getRating() < 1 || post.getRating() > 5) {
            throw new IllegalArgumentException("rating debe estar entre 1 y 5");
        }
        // negocio activo
        Business biz = post.getReviewedBusiness();
        if (!(biz.isActive())) { // si tu getter es getIsActive(), cámbialo aquí
            throw new IllegalArgumentException("El negocio no está activo");
        }
        // unicidad autor-negocio
        if (postRepository.existsByAuthorAndReviewedBusiness(post.getAuthor(), biz)) {
            throw new IllegalArgumentException("El autor ya reseñó ese negocio");
        }
        return postRepository.save(post);
    }

    // Helper para creación por IDs (desde controller con ids crudos)
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

        return create(p); // aplica reglas arriba
    }

    // ---------- READ ----------
    @Transactional(readOnly = true)
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Post getById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Post> getByReviewedBusiness(Long businessId, Pageable pageable) {
        Business biz = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
        return postRepository.findAllByReviewedBusiness(biz, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getByAuthor(Long authorUserId, Pageable pageable) {
        User author = userRepository.findById(authorUserId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Opción A (recomendada): si agregaste Page en el repo
        try {
            return postRepository.findAllByAuthor(author, pageable);
        } catch (Throwable ignore) { /* cae a fallback si no existe el método */ }

        // Opción B (fallback sin tocar el repo): lista completa -> Page manual
        List<Post> all = postRepository.findByAuthor(author);
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), all.size());
        if (start > end) return new PageImpl<>(List.of(), pageable, all.size());
        return new PageImpl<>(all.subList(start, end), pageable, all.size());
    }

    // ---------- UPDATE ----------
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
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post no encontrado con id: " + id);
        }
        postRepository.deleteById(id);
    }
}
