package com.mexazon.app.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "posts",
    indexes = {
        @Index(name = "idx_posts_author", columnList = "author_user_id"),
        @Index(name = "idx_posts_business", columnList = "reviewed_business_id")
    }
)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;
    // Autor del post -> users.user_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_user_id", nullable = false, updatable = false)
    private User author;

    // Negocio reseñado -> businesses.business_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewed_business_id", nullable = false, updatable = false)
    private Business reviewedBusiness;

    @Column(name = "rating", nullable = false)
    private int rating; // o Integer si prefieres

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

    // --- Getters & Setters ---
    public Long getPostId() { return postId; }

    public User getAuthor() { return author; }

    public Business getReviewedBusiness() { return reviewedBusiness; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    
}
