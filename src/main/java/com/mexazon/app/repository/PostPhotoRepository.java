package com.mexazon.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mexazon.app.model.PostPhoto;

@Repository
public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {

    // 🔍 Encuentra todas las fotos de un post específico
    List<PostPhoto> findByPostId_PostId(Long postId);

    // 📸 Encuentra una foto específica por orden dentro del post
    PostPhoto findByPostId_PostIdAndPhotoOrder(Long postId, Short photoOrder);
}