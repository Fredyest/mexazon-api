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

	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
	private List<Post> postsAuthored;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserType() {
		return userType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public List<Post> getPostsAuthored() {
		return postsAuthored;
	}

}  
