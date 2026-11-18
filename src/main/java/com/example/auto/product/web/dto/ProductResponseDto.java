package com.example.auto.product.web.dto;

import com.example.auto.product.domain.Product;

import java.math.BigDecimal;

public class ProductResponseDto {

    private final Long id;
    private final String name;
    private final String category;
    private final String carBrand;
    private final BigDecimal price;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.carBrand = product.getCarBrand();
        this.price = product.getPrice();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
