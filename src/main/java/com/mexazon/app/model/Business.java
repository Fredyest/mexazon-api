package com.mexazon.app.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "businesses")
public class Business{
//No estan duplicandose los ID?

	 	@Id
	    @Column(name = "business_id")
	    private Long businessId;

	    @Column(name = "is_active", nullable = false)
	    private boolean isActive;

	    // 🔗 Relación: cada negocio pertenece a un usuario
	    @OneToOne
	    @MapsId 
	    @JoinColumn(name = "user_id", nullable = false, unique = true)
	    private User user;
	    
	    @OneToMany(mappedBy = "reviewedBusiness", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
		private List<Post> reviews;
	    
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