package com.mexazon.app.repository;

import com.mexazon.app.service.impl.Dish;
import com.mexazon.app.service.impl.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

	
	List<Dish> findAllByBusiness_BusinessId(Long businessId);
	
	 List<Dish> findAllByCategory_CategoryId(Long categoryId);
	 
}
