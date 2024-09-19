// package com.example.ims.Services;

// import java.util.ArrayList;
// import java.util.List;

// // import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// // import com.example.ims.Controller.ProductController;
// import com.example.ims.Module.Products;
// import com.example.ims.Repository.CategoryRepository;
// // import com.example.ims.Repository.ProductRepository;

// @Service
// public class ProductPost {
    
    
//     private final CategoryRepository categoryRepository;

//     public ProductPost(CategoryRepository categoryRepository){
//         this.categoryRepository=categoryRepository;
//     }

//     // @Autowired
//     // private ProductRepository productRepository;


//     public List<String> validate(Products products){
//         List<String> errors = new ArrayList<>();
        
//         if (categoryPresent(products)){
//             return errors;
            
//         }
//         else{
//             errors.add("No Category found");
//             return errors;
//         }
//     //    int categoryid= product.getCategoryId();
       
//     }
//     private boolean categoryPresent(Products products){
//         return categoryRepository.existsById(products.getCategory_id());
    

// }
// }
