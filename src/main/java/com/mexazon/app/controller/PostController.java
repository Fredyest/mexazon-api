package com.mexazon.app.controller;

import com.mexazon.app.model.Post;
import com.mexazon.app.service.impl.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // Listado por negocio (paginado)
    @GetMapping("/businesses/{businessId}/posts")
    public Page<Post> byBusiness(@PathVariable Long businessId,
                                 @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
                                 Pageable pageable) {
        return service.getByReviewedBusiness(businessId, pageable);
    }

    // Listado por autor (paginado)
    @GetMapping("/users/{userId}/posts")
    public Page<Post> byAuthor(@PathVariable Long userId,
                               @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
                               Pageable pageable) {
        return service.getByAuthor(userId, pageable);
    }

    // Crear post (con IDs crudos)
    @PostMapping("/posts")
    public Post create(@RequestBody Map<String, Object> body) {
        Long authorId   = Long.valueOf(body.get("authorUserId").toString());
        Long businessId = Long.valueOf(body.get("reviewedBusinessId").toString());
        int rating      = Integer.parseInt(body.get("rating").toString());
        String description = (String) body.get("description");
        return service.createByIds(authorId, businessId, rating, description);
    }

    // Actualizar rating/descripcion
    @PutMapping("/posts/{postId}")
    public Post update(@PathVariable Long postId, @RequestBody Map<String, Object> body) {
        Integer rating        = body.get("rating") == null ? null : Integer.valueOf(body.get("rating").toString());
        String description    = (String) body.get("description");
        if (rating == null) throw new IllegalArgumentException("rating es obligatorio para actualizar");
        return service.update(postId, rating, description);
    }

    // Eliminar post
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        service.delete(postId);
    }
}

