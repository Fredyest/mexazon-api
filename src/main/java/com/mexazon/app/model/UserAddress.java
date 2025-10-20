package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users_address")
public class UserAddress {

	 @Id
	    private Long userId;

	    @OneToOne(fetch = FetchType.LAZY)
	    @MapsId
	    @JoinColumn(name = "user_id", nullable = false, unique = true)
	    private User user;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumns({
	        @JoinColumn(name = "postal_code", referencedColumnName = "postal_code"),
	        @JoinColumn(name = "colonia", referencedColumnName = "colonia")
	    })
	    private PostalCodeCatalog postalCode;

	    @Column(name = "street", length = 100, nullable = false)
	    private String street;

	    @Column(name = "number", length = 10, nullable = false)
	    private String number;

    // Getters y Setters
    // userId
    public Long getUserId() {
        return userId;
    }
    // postalCode
    public PostalCodeCatalog getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(PostalCodeCatalog postalCode) {
        this.postalCode = postalCode;
    }


    // street
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    // number
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

