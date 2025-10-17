package com.mexazon.app.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table(name="posts")
public class Post {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long postId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
	private User authorUserId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_business_id", nullable = false)
	private Business reviewedBusinessId;
	
	@Column(name = "rating", precision = 1, nullable = true, updatable = true)
	private int rating;
	@Column(name = "description", length = 255, nullable = true, updatable = true)
	private String description;
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "post_id", cascade = CascadeType.ALL)
    private List<PostPhoto> post_photos;
	
}
