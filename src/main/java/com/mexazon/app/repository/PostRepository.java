package com.mexazon.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Post;
import com.mexazon.app.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

     // Buscar todos los posts de un usuario (autor)
    List<Post> findByAuthor(User author);

    //  Buscar todos los posts asociados a un negocio reseñado
    List<Post> findByReviewedBusiness(Business business);

}
