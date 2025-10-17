package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "postal_code_catalog")
public class PostalCodeCatalog {
    @EmbeddedId
    private PostalCodeId id;

    @Column(name = "alcaldia", length = 100, nullable = false)
    private String alcaldia;

    // Getters y Setters
    // alcaldia
    public PostalCodeId getId() {
        return id;
    }

    public void setId(PostalCodeId id) {
        this.id = id;
    }

    // alcaldia
    public String getAlcaldia() {
        return alcaldia;
    }

    public void setAlcaldia(String alcaldia) {
        this.alcaldia = alcaldia;
    }

}
