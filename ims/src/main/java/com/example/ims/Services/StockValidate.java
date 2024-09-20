package com.example.ims.Services;

import org.springframework.stereotype.Service;

// import org.springframework.beans.factory.annotation.Autowired;

import com.example.ims.Module.Orderdto;
import com.example.ims.Module.Products;
// import com.example.ims.Repository.ProductRepository;
@Service
public class StockValidate {

    // @Autowired
    // private ProductRepository productRepository;

    public boolean validate(Products product,Orderdto orderdto){
        if(product.getQuantity()>=orderdto.getQuantity()){
            int val=product.getQuantity()-orderdto.getQuantity();
            product.setQuantity(val);
            return true;
        }
        else{
            return false;
        }
    }
    
}
