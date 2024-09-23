package com.example.ims.Services;

import java.util.List;
import java.util.Map;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ims.Module.Category;
import com.example.ims.Module.Products;
import com.example.ims.Module.ResponseMessage;
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
            List<Products>temp= productRepository.findByCategory_id(category_id);
            if(!temp.isEmpty()){
                return temp;
            }
            else{
                return Map.of("message","Invalid category id");
            }
        }
        else if (product_id!=null){
           Optional<Products> temp=productRepository.findById(product_id);
           if(temp.isPresent()){
            return temp;
           }
           else{
            return Map.of("message","Invalid product id");
           }
        }
        else{return Map.of("message","An error occured");}
        
        

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

    public ResponseEntity<ResponseMessage> deleteCategory(Integer category_id) {
        if(categoryRepository.existsById(category_id)){
            Optional<Category> category=categoryRepository.findById(category_id);
            if(category.isPresent()){
                Category categoryDetails=category.get();
                if(productRepository.findByCategory_id(category_id).isEmpty()){
                    categoryRepository.deleteById(category_id);
                    ResponseMessage responseMessage=new ResponseMessage("Successfully deleted " +categoryDetails.getCategory_name()+ " from the Inventory management system");
                    return ResponseEntity.ok(responseMessage);
                }
                else{
                    ResponseMessage responseMessage=new ResponseMessage("Cannot delete " +categoryDetails.getCategory_name()+ " because it has products under it");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);                    
                }
            }
            else{
                ResponseMessage responseMessage=new ResponseMessage("Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
        }

        else{
            ResponseMessage responseMessage=new ResponseMessage("Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }
    }

    public ResponseEntity<ResponseMessage> deleteProduct(Integer productId) {
        if(productRepository.existsById(productId)){
            Optional<Products> product=productRepository.findById(productId);
            if(product.isPresent()){
                productRepository.deleteById(productId);
                ResponseMessage responseMessage=new ResponseMessage("Successfully deleted " +product.get().getProduct_name()+ " from the Inventory management system");
                return ResponseEntity.ok(responseMessage);
            }
            else{
                ResponseMessage responseMessage=new ResponseMessage("Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
        }
        else{
            ResponseMessage responseMessage=new ResponseMessage("Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }
    }

    public ResponseEntity<ResponseMessage> updateCategory(Integer category_id,String category_name) {

        if(categoryRepository.existsById(category_id)){
            Optional<Category> category=categoryRepository.findById(category_id);
            if(category.isPresent()){
                category.get().setCategory_name(category_name);
                categoryRepository.save(category.get());
                ResponseMessage responseMessage=new ResponseMessage("Category updated successfully");
                return ResponseEntity.ok(responseMessage);
            }
            else{
                ResponseMessage responseMessage=new ResponseMessage("Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
        }

        else{
            ResponseMessage responseMessage=new ResponseMessage("Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }

    }

    public ResponseEntity<ResponseMessage> updateProduct(Integer productId, String productName, Integer categoryId,
            Double price, Integer quantity) {

        if (productRepository.existsById(productId)) {
            Optional<Products> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isPresent()) {
                Products product = optionalProduct.get();
                
                if (quantity != null && quantity < 0) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseMessage("Quantity cannot be negative"));
                }

                if (price != null && price < 0) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseMessage("Price cannot be negative"));
                }
    
                if (productName != null) {
                    product.setProduct_name(productName);;
                }
                if (categoryId != null) {
                    Optional<Category> category = categoryRepository.findById(categoryId);
                    if (category.isPresent()) {
                        product.setCategory(category.get());
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseMessage("Category not found"));
                    }
                }
                if (price != null) {
                    product.setPrice(price);
                }
                if (quantity != null) {
                    product.setQuantity(quantity);
                }

                // Save the updated product
                productRepository.save(product);
                return ResponseEntity.ok(new ResponseMessage("Product updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage("Product not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Product not found"));
        }
    }

    
}
