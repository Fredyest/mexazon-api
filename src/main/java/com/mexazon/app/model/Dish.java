package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity 
@Table(name="dishes")
public class Dish {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dishId;
    @Column(name = "business_id", nullable = false)
	private Long businessId;
	@Column(name = "category_id", nullable = false)
	private Long categoryId;
	@Column(name = "dish_name", length = 100, nullable = false)
	private String dishName;
	@Column(name = "description", length = 255, nullable = false)
	private String description;
	@Column(name = "price", nullable = false)
	private Double price;
	@Column(name = "photo_url", length = 255)
	private String photoUrl;

    // Getters y Setters
    // dishId
     public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    // businessId
    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    // categoryId
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    // dishName
    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    // description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // price
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // photoUrl
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
