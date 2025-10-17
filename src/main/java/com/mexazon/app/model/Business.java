package com.mexazon.app.model;

impor jakarta.persistence.*;

@Entity
@Table(name = "business")
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
