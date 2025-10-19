package com.mexazon.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.MenuCategory;


@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    
	List<MenuCategory> findAllByCategoryNameContainingIgnoreCase(String categoryName);

}
