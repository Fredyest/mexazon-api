package com.mexazon.app.model;

import jakarta.persistence.*;

@Entity 
@Table(name="dishes")
public class Dish {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dishId;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_dish_business"))
	private Business business;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_dish_category"))
	private MenuCategory category;
    
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

    // businessId
    public Long getBusinessId() {
        return business.getBusinessId();
    }

    // categoryId
    public Long getCategoryId() {
        return category.getCategoryId();
    }

    public void setCategoryId(MenuCategory categoryId) {
        this.category = categoryId;
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
