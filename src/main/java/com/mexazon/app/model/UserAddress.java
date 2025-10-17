package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users_address")
public class UserAddress {

    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // postalCode
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
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

