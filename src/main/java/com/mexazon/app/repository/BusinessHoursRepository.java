package com.mexazon.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.BusinessHours;
import com.mexazon.app.model.BusinessHoursId;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, BusinessHoursId> {

    // Todos los horarios de un negocio
    List<BusinessHours> findByBusiness(Business business);
}