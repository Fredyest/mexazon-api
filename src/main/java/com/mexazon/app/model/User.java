package com.mexazon.app.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;

/**
 * Representa un usuario registrado en la plataforma Mexazón.
 * <p>
 * Esta entidad unifica la información de todos los tipos de usuario:
 * tanto los usuarios ordinarios (clientes) como los negocios.
 * La diferenciación entre ellos se realiza mediante el campo {@code userType}.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>La tabla {@code users} funciona como tabla base para identidad y autenticación.</li>
 *   <li>El campo {@code userType} permite distinguir entre un usuario ordinario y un negocio.</li>
 *   <li>Se definen restricciones únicas para {@code email} y {@code phone}.</li>
 *   <li>Se utiliza {@link CreationTimestamp} para generar automáticamente la fecha de registro.</li>
 *   <li>El campo {@code password} almacena el hash de la contraseña, nunca en texto plano.</li>
 * </ul>
 *
 * <h3>Relaciones futuras:</h3>
 * <ul>
 *   <li>Uno a uno con {@code Business}, si el usuario representa un negocio.</li>
 *   <li>Uno a muchos con {@code Post}, si el usuario realiza reseñas.</li>
 * </ul>
 *
 * <p><strong>Tabla:</strong> {@code users}</p>
 */
@Entity
@Table(name = "users")
public class User {

    // =====================
    // CAMPOS PRINCIPALES
    // =====================

    /**
     * Identificador único del usuario.
     * <p>
     * Se genera automáticamente mediante {@link GenerationType#IDENTITY}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * Tipo de usuario dentro del sistema.
     * <p>
     * Los valores válidos son {@code ordinary} (usuarios regulares)
     * y {@code business} (usuarios con perfil de negocio).
     * </p>
     */
    @Column(name = "user_type", nullable = false)
    private String userType;

    /**
     * Correo electrónico único asociado al usuario.
     * <p>
     * Se utiliza como identificador principal para el inicio de sesión.
     * </p>
     */
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    /**
     * Hash de la contraseña del usuario.
     * <p>
     * No debe almacenarse la contraseña en texto plano.
     * El valor aquí guardado corresponde a la versión encriptada.
     * </p>
     */
    @Column(name = "password_hash", length = 255, nullable = false)
    private String password;

    /**
     * Número telefónico único del usuario.
     * <p>
     * Puede usarse para contacto o autenticación de dos factores.
     * </p>
     */
    @Column(name = "phone", length = 15, nullable = false, unique = true)
    private String phone;

    /**
     * Nombre completo o nombre comercial del usuario.
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * Descripción opcional del usuario o negocio.
     * <p>
     * Puede incluir biografía, misión o presentación corta.
     * </p>
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Fecha y hora en que el usuario fue registrado.
     * <p>
     * Se asigna automáticamente al momento de la creación del registro.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * URL del avatar o foto de perfil del usuario.
     * <p>
     * Puede apuntar a un recurso externo (CDN o almacenamiento en la nube).
     * </p>
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    // =====================
    // Getters y Setters
    // =====================

    /** @return Identificador único del usuario. */
    public Long getUserId() { return userId; }

    /** @param userId asigna el identificador del usuario. */
    public void setUserId(Long userId) { this.userId = userId; }

    /** @return Tipo de usuario (ordinary o business). */
    public String getUserType() { return userType; }

    /** @param userType define el tipo de usuario. */
    public void setUserType(String userType) { this.userType = userType; }

    /** @return Correo electrónico único del usuario. */
    public String getEmail() { return email; }

    /** @param email asigna el correo electrónico del usuario. */
    public void setEmail(String email) { this.email = email; }

    /** @return Hash de la contraseña. */
    public String getPassword() { return password; }

    /** @param password establece el hash de la contraseña. */
    public void setPassword(String password) { this.password = password; }

    /** @return Número telefónico del usuario. */
    public String getPhone() { return phone; }

    /** @param phone asigna el número telefónico del usuario. */
    public void setPhone(String phone) { this.phone = phone; }

    /** @return Nombre o razón social del usuario. */
    public String getName() { return name; }

    /** @param name define el nombre o razón social del usuario. */
    public void setName(String name) { this.name = name; }

    /** @return Descripción del usuario o negocio. */
    public String getDescription() { return description; }

    /** @param description establece la descripción del usuario o negocio. */
    public void setDescription(String description) { this.description = description; }

    /** @return Fecha y hora de creación del usuario. */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt asigna manualmente la fecha de creación (no recomendado). */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @return URL del avatar del usuario. */
    public String getAvatarUrl() { return avatarUrl; }

    /** @param avatarUrl define la URL del avatar. */
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}