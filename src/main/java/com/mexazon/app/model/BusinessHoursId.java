package com.mexazon.app.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BusinessHoursId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    // 0 = Lunes, 6 = Domingo (por ejemplo)
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    // ---------- Constructores ----------
    public BusinessHoursId() {
    }

    public BusinessHoursId(Long businessId, Integer dayOfWeek) {
        this.businessId = businessId;
        this.dayOfWeek = dayOfWeek;
    }

    // ---------- Getters & Setters ----------
    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    // ---------- equals() y hashCode() ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessHoursId that = (BusinessHoursId) o;
        return Objects.equals(businessId, that.businessId) &&
               Objects.equals(dayOfWeek, that.dayOfWeek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessId, dayOfWeek);
    }
}
