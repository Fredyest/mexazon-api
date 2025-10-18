package com.mexazon.app.model;

import java.io.Serializable;
import jakarta.persistence.*;

@Embeddable
public class PostalCodeId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;

    @Column(name = "colonia", length = 100, nullable = false)
    private String colonia;

    // Constructor vacío
    public PostalCodeId() {}

    public PostalCodeId(String postalCode, String colonia) {
        this.postalCode = postalCode;
        this.colonia = colonia;
    }

    // Getters y Setters
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    // equals y hashCode (requeridos para claves compuestas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostalCodeId)) return false;
        PostalCodeId that = (PostalCodeId) o;
        return postalCode.equals(that.postalCode) && colonia.equals(that.colonia);
    }

    @Override
    public int hashCode() {
        return postalCode.hashCode() + colonia.hashCode();
    }
 
}
