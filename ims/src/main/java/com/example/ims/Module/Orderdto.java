package com.example.ims.Module;

import jakarta.validation.constraints.Min;

public class Orderdto {

    private int product_id;
   
    private int userid;

    @Min(value = 1,message = "Quantity should not be negative or zero")
    private int quantity;

    
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    
}
