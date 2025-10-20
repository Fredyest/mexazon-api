package com.mexazon.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.User;
import com.mexazon.app.model.UserAddress;
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // by PK
    Optional<UserAddress> findByUserId(Long userId);

    // by relation
    Optional<UserAddress> findByUser(User user);

    // ---- queries by postal code / colonia (navigate embedded id) ----
    List<UserAddress> findAllByPostalCodeIdPostalCode(String postalCode);
    List<UserAddress> findAllByPostalCodeIdColonia(String colonia);

    // both parts of the composite key
    List<UserAddress> findAllByPostalCodeIdPostalCodeAndPostalCodeIdColonia(String postalCode, String colonia);
}