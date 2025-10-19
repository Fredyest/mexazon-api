package com.mexazon.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mexazon.app.model.Business;
import com.mexazon.app.model.User;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    // Buscar un negocio por su usuario
    Business findByUser(User user);

    // Buscar negocios activos
    List<Business> findByIsActiveTrue();

    // Buscar negocios inactivos
    List<Business> findByIsActiveFalse();
}
