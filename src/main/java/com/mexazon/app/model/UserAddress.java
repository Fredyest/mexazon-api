package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users_address")
public class UserAddress {

    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @OneToOne
    @MapsId 
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postal_code", referencedColumnName = "postal_code")
    private PostalCodeCatalog postalCode;
    @Column(name = "colonia", length = 100, nullable = false)
    private String colonia;
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

    // colonia
    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
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

