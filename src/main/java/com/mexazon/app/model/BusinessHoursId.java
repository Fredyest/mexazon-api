package com.mexazon.app.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class BusinessHoursId implements Serializable {

    @Column(name = "business_id")
    private Long businessId;

    @Enumerated(EnumType.STRING) // Mapea el ENUM de SQL como String en Java
    @Column(name = "day_of_week", length = 3, nullable = false)
    private DayOfWeek dayOfWeek;
    
    // Es necesario tener un constructor sin argumentos, getters, setters,
    // y sobreescribir equals() y hashCode() para que funcione correctamente.
}

// Un ENUM para los días de la semana
enum DayOfWeek {
    Mon, Tue, Wed, Thu, Fri, Sat, Sun
}