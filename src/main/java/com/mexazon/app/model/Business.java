package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "businesses")
public class Business {

    @Id
    @Column(name = "business_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId 
    @JoinColumn(name = "business_id")
    private User user;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
