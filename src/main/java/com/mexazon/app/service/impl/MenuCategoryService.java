package com.mexazon.app.service.impl;

import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.repository.MenuCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuCategoryService {

    @Autowired
    private MenuCategoryRepository repository;

    public List<MenuCategory> findAll() {
        return repository.findAll();
    }

    public Optional<MenuCategory> findById(Long id) {
        return repository.findById(id);
    }

    public MenuCategory save(MenuCategory menuCategory) {
        // Validaciones o lógica de negocio antes de guardar.
        return repository.save(menuCategory);
    }

    public MenuCategory update(Long id, MenuCategory updatedMenuCategory) {
        Optional<MenuCategory> existing = repository.findById(id);
        if (existing.isPresent()) {
            MenuCategory entity = existing.get();
            // Actualiza los campos relevantes según tu modelo.
            entity.setCategoryName(updatedMenuCategory.getCategoryName());
            // agrega más setters según el modelo
            return repository.save(entity);
        } else {
            throw new RuntimeException("MenuCategory no encontrada con id " + id);
        }
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

