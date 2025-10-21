package com.mexazon.app.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.BusinessHours;
import com.mexazon.app.model.BusinessHoursId;
import com.mexazon.app.model.WeekDay;
import com.mexazon.app.repository.BusinessHoursRepository;

/**
 * Servicio para gestionar los horarios de negocio.
 * Proporciona operaciones CRUD para los horarios de apertura y cierre
 * de los negocios por día de la semana.
 */
@Service
@Transactional
public class BusinessHoursService {

    private final BusinessHoursRepository repository;

    /**
     * Constructor del servicio de horarios de negocio.
     *
     * @param repository Repositorio de horarios de negocio
     */
    public BusinessHoursService(BusinessHoursRepository repository) {
        this.repository = repository;
    }

    // ---------- CREATE ----------
    /**
     * Crea un nuevo horario para un negocio en un día específico.
     * Valida que la hora de cierre sea posterior a la hora de apertura
     * y que no exista un horario previo para ese negocio y día.
     *
     * @param business Negocio al que pertenece el horario
     * @param day Día de la semana
     * @param timeIn Hora de apertura
     * @param timeOut Hora de cierre
     * @param isWorking Indica si el negocio está abierto ese día
     * @return El horario creado
     * @throws IllegalArgumentException si time_out no es posterior a time_in
     * @throws IllegalArgumentException si ya existe un horario para ese negocio y día
     */
    public BusinessHours create(Business business, 	WeekDay day,
                                LocalTime timeIn, LocalTime timeOut, boolean isWorking) {

        if (isWorking && timeIn != null && timeOut != null && !timeOut.isAfter(timeIn)) {
            throw new IllegalArgumentException("time_out debe ser posterior a time_in");
        }

        BusinessHoursId id = new BusinessHoursId(business.getBusinessId(), day);
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Ya existe horario para ese negocio y día");
        }

        BusinessHours bh = new BusinessHours();
        bh.setId(id);
        bh.setBusiness(business);
        bh.setWorking(isWorking);
        bh.setTimeIn(timeIn);
        bh.setTimeOut(timeOut);

        return repository.save(bh);
    }

    // ---------- READ ----------
    /**
     * Obtiene el horario de un negocio para un día específico.
     *
     * @param businessId ID del negocio
     * @param dayOfWeek Día de la semana (0-6, donde 0 es el primer día)
     * @return El horario encontrado
     * @throws IllegalArgumentException si no se encuentra el horario
     */
    @Transactional(readOnly = true)
    public BusinessHours get(Long businessId, int dayOfWeek) {
        return repository.findById(new BusinessHoursId(businessId, WeekDay.values()[dayOfWeek]))
            .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
    }

    /**
     * Obtiene todos los horarios de un negocio (todos los días de la semana).
     *
     * @param business Negocio del cual obtener los horarios
     * @return Lista de horarios del negocio
     */
    @Transactional(readOnly = true)
    public List<BusinessHours> getAllByBusiness(Business business) {
        return repository.findByBusiness(business);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza el horario de un negocio para un día específico.
     * Permite actualizar parcialmente los campos (null mantiene el valor actual).
     * Valida que la hora de cierre sea posterior a la hora de apertura.
     *
     * @param businessId ID del negocio
     * @param dayOfWeek Día de la semana (0-6)
     * @param timeIn Nueva hora de apertura (null para mantener la actual)
     * @param timeOut Nueva hora de cierre (null para mantener la actual)
     * @param isWorking Nuevo estado de apertura (null para mantener el actual)
     * @return El horario actualizado
     * @throws IllegalArgumentException si time_out no es posterior a time_in
     * @throws IllegalArgumentException si no se encuentra el horario
     */
    public BusinessHours update(Long businessId, int dayOfWeek,
                                LocalTime timeIn, LocalTime timeOut, Boolean isWorking) {

        BusinessHours existing = get(businessId, dayOfWeek);

        if (isWorking != null) existing.setWorking(isWorking);
        if (timeIn != null) existing.setTimeIn(timeIn);
        if (timeOut != null) existing.setTimeOut(timeOut);

        if (existing.isWorking()
                && existing.getTimeIn() != null
                && existing.getTimeOut() != null
                && !existing.getTimeOut().isAfter(existing.getTimeIn())) {
            throw new IllegalArgumentException("time_out debe ser posterior a time_in");
        }

        return repository.save(existing);
    }

    /**
     * Marca un día como cerrado para un negocio.
     * Establece isWorking en false y limpia las horas de apertura y cierre.
     *
     * @param businessId ID del negocio
     * @param dayOfWeek Día de la semana (0-6)
     * @return El horario actualizado
     * @throws IllegalArgumentException si no se encuentra el horario
     */
    public BusinessHours close(Long businessId, int dayOfWeek) {
        BusinessHours existing = get(businessId, dayOfWeek);
        existing.setWorking(false);
        existing.setTimeIn(null);
        existing.setTimeOut(null);
        return repository.save(existing);
    }

    // ---------- DELETE ----------
    /**
     * Elimina el horario de un negocio para un día específico.
     *
     * @param businessId ID del negocio
     * @param dayOfWeek Día de la semana (0-6)
     * @throws IllegalArgumentException si no se encuentra el horario
     */
    public void delete(Long businessId, int dayOfWeek) {
        BusinessHoursId id = new BusinessHoursId(businessId, WeekDay.values()[dayOfWeek]);
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Horario no encontrado");
        }
        repository.deleteById(id);
    }
}

