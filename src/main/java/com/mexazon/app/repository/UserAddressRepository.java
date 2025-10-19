package com.mexazon.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mexazon.app.model.UserAddress;
import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // Busca todas las direcciones que estén en una ciudad específica
    List<UserAddress> findAllByCityIgnoreCase(String city);

    // Busca todas las direcciones por CP
    List<UserAddress> findAllByZipCode(String zipCode);

    // Si quisieras combinar ambos criterios:
    List<UserAddress> findAllByCityIgnoreCaseAndZipCode(String city, String zipCode);
}
