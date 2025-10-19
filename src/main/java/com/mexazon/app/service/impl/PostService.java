package com.mexazon.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mexazon.app.model.Post;
import com.mexazon.app.model.User;
import com.mexazon.app.model.Business;
import com.mexazon.app.repository.PostRepository;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // ---------- CREATE ----------
    public Post create(Post post) {
        if (post.getAuthor() == null || post.getReviewedBusiness() == null) {
            throw new IllegalArgumentException("El autor y el negocio reseñado son obligatorios");
        }
        return postRepository.save(post);
    }

    // ---------- READ ----------
    @Transactional(readOnly = true)
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Post getById(Integer id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Post> getByAuthor(User author) {
        return postRepository.findByAuthor(author);
    }

    @Transactional(readOnly = true)
    public List<Post> getByReviewedBusiness(Business business) {
        return postRepository.findByReviewedBusiness(business);
    }

    // ---------- UPDATE ----------
    public Post update(Integer id, int rating, String description) {
        Post existing = getById(id);
        existing.setRating(rating);
        existing.setDescription(description);
        return postRepository.save(existing);
    }

    // ---------- DELETE ----------
    public void delete(Integer id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post no encontrado con id: " + id);
        }
        postRepository.deleteById(id);
    }
}

