package com.mexazon.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.MenuCategory;
import com.sun.tools.javac.util.List;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    
	List<MenuCategory> findAllByCategoryNameContainingIgnoreCase(String categoryName);

}
