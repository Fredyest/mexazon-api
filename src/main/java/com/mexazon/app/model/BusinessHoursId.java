package com.mexazon.app.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BusinessHoursId implements Serializable {

    @Column(name = "business_id")
    private Long businessId;

    // Cambiamos el enum por un Integer para manejar 0-6
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    // Recuerda generar el constructor vacío, getters, setters, equals() y hashCode()
}