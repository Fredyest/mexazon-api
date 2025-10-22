package com.mexazon.app.model;

import jakarta.persistence.*;

/**
 * Representa un negocio registrado dentro de la plataforma Mexazón.
 * <p>
 * Esta entidad complementa al {@link User} cuando el tipo de usuario es {@code business}.
 * Su identificador principal (PK) comparte el mismo valor que el {@code userId} de la entidad {@link User},
 * gracias al uso de la anotación {@link MapsId}.
 * </p>
 *
 * <h3>Relaciones principales:</h3>
 * <ul>
 *   <li>Uno a uno con {@link User}: Cada negocio está asociado exactamente a un usuario.</li>
 *   <li>Uno a muchos con {@code BusinessHour}: Define los horarios en los que el negocio está abierto.</li>
 * </ul>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Se reutiliza el mismo ID del usuario para garantizar integridad y simplicidad en la relación.</li>
 *   <li>El campo {@code isActive} indica si el negocio está activo en la plataforma (por defecto, {@code true}).</li>
 *   <li>Se define carga perezosa ({@code LAZY}) para la relación con {@link User}, 
 *       ya que la información del usuario no siempre es necesaria al consultar negocios.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code businesses}</p>
 */
@Entity
@Table(name = "businesses")
public class Business {

    /**
     * Identificador único del negocio.
     * <p>
     * Este ID es compartido con el {@link User}, 
     * lo que significa que el negocio no puede existir sin un usuario asociado.
     * </p>
     */
    @Id
    @Column(name = "business_id")
    private Long businessId;

    /**
     * Relación uno a uno con el usuario correspondiente.
     * <p>
     * Se utiliza {@link MapsId} para compartir el mismo ID de la entidad {@link User}.
     * </p>
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "business_id")
    private User user;

    /**
     * Indica si el negocio está activo dentro de la plataforma.
     * <p>
     * Este campo se usa para habilitar o suspender temporalmente un negocio sin eliminar su registro.
     * </p>
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador único del negocio. */
    public Long getBusinessId() {
        return businessId;
    }

    /** @param businessId asigna el identificador único del negocio. */
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    /** @return Usuario asociado a este negocio. */
    public User getUser() {
        return user;
    }

    /** @param user establece el usuario al que pertenece este negocio. */
    public void setUser(User user) {
        this.user = user;
    }

    /** @return {@code true} si el negocio está activo en la plataforma. */
    public boolean isActive() {
        return isActive;
    }

    /** @param active define si el negocio está activo o no. */
    public void setActive(boolean active) {
        this.isActive = active;
    }
}