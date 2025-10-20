package com.mexazon.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Post;
import com.mexazon.app.model.User;
@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
	   
    // Buscar todos los posts de un autor específico
    List<Post> findByAuthor(User author);

    // Buscar posts de un negocio específico
    List<Post> findByReviewedBusiness(Business business);

    // Buscar posts creados después de cierta fecha (útil para filtrar los más nuevos)
    List<Post> findByCreatedAtAfter(LocalDateTime from);

    // Buscar por autor + negocio (por si quieres evitar duplicar reseñas)
    boolean existsByAuthorAndReviewedBusiness(User author, Business business);

    // Buscar todos los posts ordenados por fecha de creación (más recientes primero)
    List<Post> findAllByOrderByCreatedAtDesc();

    // Buscar posts con una calificación específica
    List<Post> findByRating(int rating);

    // Paginación (útil para feed o scroll infinito)
    Page<Post> findAllByReviewedBusiness(Business business, Pageable pageable);

    // --- Consulta personalizada: promedio de calificación de un negocio ---
    @Query("SELECT AVG(p.rating) FROM Post p WHERE p.reviewedBusiness = :business")
    Double getAverageRating(@Param("business") Business business);

    // --- Consulta personalizada: obtener posts con datos del autor ---
    @EntityGraph(attributePaths = {"author", "reviewedBusiness"})
    @Query("SELECT p FROM Post p WHERE p.reviewedBusiness.businessId = :businessId")
    List<Post> findAllByBusinessWithAuthor(@Param("businessId") Long businessId);

    // --- Obtener todos los posts recientes (últimos N días) ---
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(@Param("startDate") LocalDateTime startDate);
}
