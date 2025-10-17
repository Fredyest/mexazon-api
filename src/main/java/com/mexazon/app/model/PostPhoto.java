package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_photos")
public class PostPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "photo_url", length = 255, nullable = false)
    private String photoUrl;

    @Column(name = "photo_order")
    private Short photoOrder;
}
