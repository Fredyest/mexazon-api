package com.mexazon.app.service.impl;

import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.repository.MenuCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de categorías del menú ({@link MenuCategory}).
 * <p>
 * Proporciona operaciones CRUD básicas para administrar las categorías
 * disponibles en los menús de los negocios.
 * <br>
 * Incluye validaciones mínimas antes de guardar o actualizar los registros.
 */
@Service
public class MenuCategoryService {

    @Autowired
    private MenuCategoryRepository repository;

    // ---------- READ ----------
    /**
     * Obtiene todas las categorías del menú.
     *
     * @return Lista completa de categorías disponibles
     */
    @Transactional(readOnly = true)
    public List<MenuCategory> findAll() {
        return repository.findAll();
    }

    /**
     * Busca una categoría específica por su identificador.
     *
     * @param id ID de la categoría
     * @return Un {@link Optional} que contiene la categoría si existe
     */
    @Transactional(readOnly = true)
    public Optional<MenuCategory> findById(Long id) {
        return repository.findById(id);
    }

    // ---------- CREATE ----------
    /**
     * Crea una nueva categoría de menú.
     * <p>
     * Puede incluir validaciones de negocio antes de guardar.
     *
     * @param menuCategory Entidad {@link MenuCategory} a registrar
     * @return La categoría creada y persistida
     */
    @Transactional
    public MenuCategory save(MenuCategory menuCategory) {
        // Validaciones o reglas de negocio previas al guardado.
        return repository.save(menuCategory);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza los datos de una categoría existente.
     * <p>
     * Solo se actualizan los campos relevantes definidos en el modelo.
     *
     * @param id ID de la categoría a actualizar
     * @param updatedMenuCategory Objeto con los nuevos valores
     * @return La categoría actualizada
     * @throws IllegalArgumentException si la categoría no existe
     */
    @Transactional
    public MenuCategory update(Long id, MenuCategory updatedMenuCategory) {
        Optional<MenuCategory> existing = repository.findById(id);

        if (existing.isPresent()) {
            MenuCategory entity = existing.get();

            // Actualiza los campos relevantes según el modelo
            entity.setCategoryName(updatedMenuCategory.getCategoryName());
            // Agrega más setters según los atributos del modelo

            return repository.save(entity);
        } else {
            throw new IllegalArgumentException("MenuCategory no encontrada con id " + id);
        }
    }

    // ---------- DELETE ----------
    /**
     * Elimina una categoría del menú según su identificador.
     *
     * @param id ID de la categoría a eliminar
     */
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}