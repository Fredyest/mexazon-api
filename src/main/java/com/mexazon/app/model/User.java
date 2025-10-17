package com.mexazon.app.model;


import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.*;

@Entity 
@Table(name="users")

public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	@Column(name = "user_type", nullable = false)
	private String userType;
	@Column(name = "email", length = 100, nullable = false, unique = true)
	private String email;
	@Column(name = "password", length = 255, nullable = false)
	private String passwordHash;
	@Column(name = "phone", length = 100, nullable = false, unique = true)
	private String phone;
	@Column(name = "name", length = 255, nullable = false)
	private String name;
	@Column(name = "description", length = 255, nullable = true)
	private String description;
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "avatar_url", length = 255, nullable = true)
	private String avatar;
	
	
	@OneToMany(mappedBy = "author_user_id", cascade = CascadeType.ALL)
    private List<Post> posts;
	
}  
