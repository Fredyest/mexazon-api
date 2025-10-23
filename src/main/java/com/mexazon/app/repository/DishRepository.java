package com.mexazon.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.Dish;
import com.mexazon.app.model.MenuCategory;

import java.util.List;
@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
	  List<Dish> findAllByCategory(MenuCategory category);
	  List<Dish> findAllByBusiness(Business business);
	  List<Dish> findAllByCategoryCategoryId(Long categoryId);
	  List<Dish> findAllByBusinessBusinessId(Long businessId);
}
