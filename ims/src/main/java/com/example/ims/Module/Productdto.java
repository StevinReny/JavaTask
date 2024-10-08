package com.example.ims.Module;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Productdto {
    
    @Column(nullable=false)
    @NotBlank(message="Product name should not be blank")
    private String product_name;
    
    @NotNull(message = "Category Id should not be null")
    private int category_id;

    @NotNull(message = "Price should not be null")
    @Min(value = 1,message = "Minimum value of price is 1")
    private double price;

    @NotNull(message = "Quantity should not be null")
    @Min(value = 0,message = "Minimum value of quantity is 0")
    private int quantity;
    
    public String getProduct_name() {
        return product_name;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    public int getCategory_id() {
        return category_id;
    }
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
