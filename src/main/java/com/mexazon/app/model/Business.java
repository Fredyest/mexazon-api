package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "businesses")
public class Business {
//No estan duplicandose los ID?

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "business_id")
	    private Long businessId;

	    @Column(name = "is_active", nullable = false)
	    private boolean isActive;

	    // 🔗 Relación: cada negocio pertenece a un usuario
	    @OneToOne
	    @JoinColumn(name = "user_id", nullable = false, unique = true)
	    private User user;

	    // --- Getters & Setters ---
	    public Long getBusinessId() {
	        return businessId;
	    }

	    public boolean isActive() {
	        return isActive;
	    }

	    public void setActive(boolean active) {
	        isActive = active;
	    }

	    public User getUser() {
	        return user;
	    }

	}