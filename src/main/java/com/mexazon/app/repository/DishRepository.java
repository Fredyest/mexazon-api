package com.mexazon.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.Dish;

import java.util.List;
@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

	
	List<Dish> findAllByBusiness_BusinessId(Long businessId);
	
	List<Dish> findAllByCategory_CategoryId(Long categoryId);
	 
}
