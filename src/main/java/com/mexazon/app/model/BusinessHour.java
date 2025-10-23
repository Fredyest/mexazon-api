package com.mexazon.app.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * Representa el horario laboral de un negocio en Mexazón.
 * <p>
 * Cada registro define los horarios de apertura y cierre para un día específico.
 * La clave primaria está compuesta por {@code businessId} y {@code dayOfWeek}.
 * </p>
 *
 * <h3>Relaciones principales:</h3>
 * <ul>
 *   <li>Muchos a uno con {@link Business}: cada negocio puede tener hasta siete registros (uno por día de la semana).</li>
 * </ul>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Se usa una clave compuesta ({@code business_id + day_of_week}) para garantizar unicidad por día.</li>
 *   <li>{@link LocalTime} permite almacenar solo la hora (sin fecha) de entrada y salida.</li>
 *   <li>El campo {@code isWorking} permite marcar días no laborables sin eliminar el registro.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code business_hours}</p>
 */
@Entity
@Table(name = "business_hours")
@IdClass(BusinessHourId.class)
public class BusinessHour implements Serializable {

    /**
     * Identificador del negocio asociado.
     * <p>
     * Forma parte de la clave primaria compuesta junto con {@code dayOfWeek}.
     * </p>
     */
    @Id
    @Column(name = "business_id")
    private Long businessId;

    /**
     * Día de la semana al que corresponde el horario.
     * <p>
     * Se utiliza un código de tres letras (por ejemplo: {@code Mon}, {@code Tue}, {@code Wed}...).
     * </p>
     */
    @Id
    @Column(name = "day_of_week", length = 3)
    private String dayOfWeek;

    /**
     * Hora de apertura del negocio.
     * <p>
     * Puede ser {@code null} si el negocio está cerrado ese día.
     * </p>
     */
    @Column(name = "time_in")
    private LocalTime timeIn;

    /**
     * Hora de cierre del negocio.
     * <p>
     * Puede ser {@code null} si el negocio está cerrado ese día.
     * </p>
     */
    @Column(name = "time_out")
    private LocalTime timeOut;

    /**
     * Indica si el negocio opera durante este día de la semana.
     * <p>
     * Si es {@code false}, el día se considera inactivo aunque exista el registro.
     * </p>
     */
    @Column(name = "is_working", nullable = false)
    private boolean isWorking = true;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador del negocio asociado. */
    public Long getBusinessId() {
        return businessId;
    }

    /** @param businessId asigna el identificador del negocio. */
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    /** @return Día de la semana (código de tres letras). */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /** @param dayOfWeek establece el día de la semana (por ejemplo, "Mon", "Tue"). */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /** @return Hora de apertura. */
    public LocalTime getTimeIn() {
        return timeIn;
    }

    /** @param timeIn define la hora de apertura. */
    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    /** @return Hora de cierre. */
    public LocalTime getTimeOut() {
        return timeOut;
    }

    /** @param timeOut define la hora de cierre. */
    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    /** @return {@code true} si el negocio trabaja ese día. */
    public boolean isWorking() {
        return isWorking;
    }

    /** @param working establece si el negocio está activo ese día. */
    public void setWorking(boolean working) {
        this.isWorking = working;
    }
}