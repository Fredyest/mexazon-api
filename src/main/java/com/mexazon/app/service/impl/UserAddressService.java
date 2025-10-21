package com.mexazon.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.User;
import com.mexazon.app.model.UserAddress;
import com.mexazon.app.repository.UserAddressRepository;

/**
 * Servicio para gestionar las direcciones de los usuarios.
 * Proporciona operaciones CRUD para las direcciones asociadas a los usuarios.
 * Cada usuario puede tener solo una dirección.
 */
@Service
@Transactional
public class UserAddressService {

    private final UserAddressRepository repository;

    /**
     * Constructor del servicio de direcciones de usuarios.
     *
     * @param repository Repositorio de direcciones de usuarios
     */
    public UserAddressService(UserAddressRepository repository) {
        this.repository = repository;
    }

    // ---------- CREATE ----------
    /**
     * Crea una nueva dirección para un usuario.
     * Valida que los campos obligatorios no estén vacíos
     * y que el usuario no tenga ya una dirección registrada.
     *
     * @param user Usuario al que pertenece la dirección
     * @param postalCode Código postal (del catálogo)
     * @param street Calle
     * @param number Número de la dirección
     * @return La dirección creada
     * @throws IllegalArgumentException si la calle está vacía
     * @throws IllegalArgumentException si el número está vacío
     * @throws IllegalArgumentException si el código postal es nulo
     * @throws IllegalArgumentException si el usuario ya tiene una dirección
     */
    public UserAddress create(User user, PostalCodeCatalog postalCode, String street, String number) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede estar vacía");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("El número no puede estar vacío");
        }
        if (postalCode == null) {
            throw new IllegalArgumentException("El código postal no puede ser nulo");
        }

        // Validar que el usuario no tenga ya una dirección
        if (repository.findByUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya tiene una dirección registrada");
        }

        UserAddress address = new UserAddress();
        address.setPostalCode(postalCode);
        address.setStreet(street);
        address.setNumber(number);

        return repository.save(address);
    }

    // ---------- READ ----------
    /**
     * Obtiene la dirección de un usuario por su ID.
     *
     * @param userId ID del usuario
     * @return La dirección del usuario
     * @throws IllegalArgumentException si no se encuentra la dirección
     */
    @Transactional(readOnly = true)
    public UserAddress get(Long userId) {
        return repository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada para el usuario"));
    }

    /**
     * Obtiene la dirección de un usuario por el objeto User.
     *
     * @param user Usuario del cual obtener la dirección
     * @return La dirección del usuario
     * @throws IllegalArgumentException si no se encuentra la dirección
     */
    @Transactional(readOnly = true)
    public UserAddress getByUser(User user) {
        return repository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada para el usuario"));
    }

    /**
     * Obtiene todas las direcciones que pertenecen a un código postal específico.
     *
     * @param postalCode Código postal a buscar
     * @return Lista de direcciones con ese código postal
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getAllByPostalCode(String postalCode) {
        return repository.findAllByPostalCodeIdPostalCode(postalCode);
    }

    /**
     * Obtiene todas las direcciones que pertenecen a una colonia específica.
     *
     * @param colonia Colonia a buscar
     * @return Lista de direcciones en esa colonia
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getAllByColonia(String colonia) {
        return repository.findAllByPostalCodeIdColonia(colonia);
    }

    /**
     * Obtiene todas las direcciones que coinciden con un código postal y colonia específicos.
     *
     * @param postalCode Código postal a buscar
     * @param colonia Colonia a buscar
     * @return Lista de direcciones que coinciden con ambos criterios
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getAllByPostalCodeAndColonia(String postalCode, String colonia) {
        return repository.findAllByPostalCodeIdPostalCodeAndPostalCodeIdColonia(postalCode, colonia);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza la dirección de un usuario.
     * Permite actualizar parcialmente los campos (null mantiene el valor actual).
     *
     * @param userId ID del usuario cuya dirección se actualizará
     * @param postalCode Nuevo código postal (null para mantener el actual)
     * @param street Nueva calle (null para mantener la actual)
     * @param number Nuevo número (null para mantener el actual)
     * @return La dirección actualizada
     * @throws IllegalArgumentException si no se encuentra la dirección
     */
    public UserAddress update(Long userId, PostalCodeCatalog postalCode, String street, String number) {
        UserAddress existing = get(userId);

        if (postalCode != null) {
            existing.setPostalCode(postalCode);
        }

        if (street != null && !street.trim().isEmpty()) {
            existing.setStreet(street);
        }

        if (number != null && !number.trim().isEmpty()) {
            existing.setNumber(number);
        }

        return repository.save(existing);
    }

    // ---------- DELETE ----------
    /**
     * Elimina la dirección de un usuario.
     *
     * @param userId ID del usuario cuya dirección se eliminará
     * @throws IllegalArgumentException si no se encuentra la dirección
     */
    public void delete(Long userId) {
        if (!repository.existsById(userId)) {
            throw new IllegalArgumentException("Dirección no encontrada");
        }
        repository.deleteById(userId);
    }
}
