package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_photos")
public class PostPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post postId;

    @Column(name = "photo_url", length = 255, nullable = false)
    private String photoUrl;

    @Column(name = "photo_order")
    private Short photoOrder;

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public Short getPhotoOrder() {
		return photoOrder;
	}

	public void setPhotoOrder(Short photoOrder) {
		this.photoOrder = photoOrder;
	}
    
    
}
