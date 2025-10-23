package com.mexazon.app.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa la clave primaria compuesta de la entidad {@link BusinessHour}.
 * <p>
 * Se utiliza junto con la anotación {@link jakarta.persistence.IdClass} para definir
 * que un registro de horario se identifica de forma única por:
 * </p>
 * <ul>
 *   <li>{@code businessId}: el negocio al que pertenece el horario.</li>
 *   <li>{@code dayOfWeek}: el día de la semana correspondiente.</li>
 * </ul>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Implementa {@link Serializable} para cumplir con los requisitos de JPA.</li>
 *   <li>Incluye métodos {@code equals()} y {@code hashCode()} consistentes, necesarios
 *       para comparar correctamente instancias dentro del contexto de persistencia.</li>
 *   <li>No se utilizan anotaciones JPA en esta clase; solo actúa como identificador auxiliar.</li>
 * </ul>
 *
 * <p><strong>Uso:</strong> Esta clase es referenciada en la entidad
 * {@link BusinessHour} mediante la anotación {@code @IdClass(BusinessHourId.class)}.</p>
 */
public class BusinessHourId implements Serializable {

    /** Identificador del negocio asociado al horario. */
    private Long businessId;

    /** Día de la semana que forma parte de la clave compuesta. */
    private String dayOfWeek;

    // =====================
    // Constructores
    // =====================

    /** Constructor vacío requerido por JPA. */
    public BusinessHourId() {}

    /**
     * Constructor conveniente para crear una instancia con valores iniciales.
     *
     * @param businessId Identificador del negocio.
     * @param dayOfWeek  Día de la semana (por ejemplo, "Mon", "Tue").
     */
    public BusinessHourId(Long businessId, String dayOfWeek) {
        this.businessId = businessId;
        this.dayOfWeek = dayOfWeek;
    }

    // =====================
    // Equals y HashCode
    // =====================

    /**
     * Compara esta instancia con otra para determinar igualdad.
     * <p>
     * Dos objetos {@code BusinessHourId} son iguales si comparten el mismo
     * {@code businessId} y el mismo {@code dayOfWeek}.
     * </p>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessHourId)) return false;
        BusinessHourId that = (BusinessHourId) o;
        return Objects.equals(businessId, that.businessId)
                && Objects.equals(dayOfWeek, that.dayOfWeek);
    }

    /**
     * Genera un hash basado en los dos atributos clave.
     * <p>
     * Este método es esencial para el correcto funcionamiento de colecciones
     * como {@link java.util.HashSet} o {@link java.util.Map}.
     * </p>
     */
    @Override
    public int hashCode() {
        return Objects.hash(businessId, dayOfWeek);
    }
}