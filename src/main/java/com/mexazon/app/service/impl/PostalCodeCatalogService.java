package com.mexazon.app.service.impl;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.PostalCodeId;
import com.mexazon.app.repository.PostalCodeCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostalCodeCatalogService {

    @Autowired
    private PostalCodeCatalogRepository repository;

    public List<PostalCodeCatalog> findAll() {
        return repository.findAll();
    }

    public Optional<PostalCodeCatalog> findById(String postalCode, String colonia) {
    	PostalCodeId id= new PostalCodeId();
    	id.setPostalCode(postalCode);
    	id.setColonia(colonia);
    	return repository.findById(id);
    }

    public PostalCodeCatalog save(PostalCodeCatalog postalCodeCatalog) {
        // Si quieres agregar validaciones, hazlo aquí.
        return repository.save(postalCodeCatalog);
    }

    public PostalCodeCatalog update(String postalCode, String colonia, PostalCodeCatalog updatedPostalCodeCatalog) {
    	PostalCodeId id= new PostalCodeId();
    	id.setPostalCode(postalCode);
    	id.setColonia(colonia);
    	Optional<PostalCodeCatalog> existing = repository.findById(id);
        if (existing.isPresent()) {
            PostalCodeCatalog entity = existing.get();
            entity.setAlcaldia(updatedPostalCodeCatalog.getAlcaldia());
            // agrega más setters según el modelo
            return repository.save(entity);
        } else {
            throw new RuntimeException("PostalCodeCatalog no encontrado con id " + id);
        }
    }

    public void delete(String postalCode, String colonia) {
    	PostalCodeId id= new PostalCodeId();
    	id.setPostalCode(postalCode);
    	id.setColonia(colonia);
        repository.deleteById(id);
    }
}
