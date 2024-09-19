package com.example.ims.Services;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ims.Module.Category;
import com.example.ims.Module.Products;
import com.example.ims.Repository.CategoryRepository;
import com.example.ims.Repository.ProductRepository;

@Service
public class GetService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired 
    private CategoryRepository categoryRepository;

    public Object formatResponse(Integer product_id,Integer category_id){
        
        if(product_id==null&&category_id==null){
            return productRepository.findAll();
        }
        else if(product_id!=null&&category_id!=null){
            return null;
        }
        else if(category_id!=null){
            return productRepository.findByCategory_id(category_id);
        }
        else if (product_id!=null){
           Optional<Products> temp=productRepository.findById(product_id);
           return temp.orElse(null);
        }
        else{return null;}
        
        

    }

    public Object formatCategoryResponse(Integer category_id){
        if(category_id==null){
            return categoryRepository.findAll();
        }
        else{
            Optional<Category> category= categoryRepository.findById(category_id);
            return category.orElse(null);
        }
    }
    
}
