package com.example.ims.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ims.Module.Category;
import com.example.ims.Module.Orderdto;
import com.example.ims.Module.Productdto;


import com.example.ims.Module.User;

import com.example.ims.Module.ResponseMessage;


import com.example.ims.Services.ImsService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ims")
public class ImsController {
    
    @Autowired
    private ImsService getService;
    

    @PostMapping("/createproduct")
    public ResponseEntity<?> createProduct(@RequestBody @Valid Productdto product,BindingResult bindingResult) {
        
        return getService.createProduct(product, bindingResult);
    
    }

    @PostMapping("/createcategory")
    public ResponseEntity<?> createcategory(@RequestBody Category category){

        return getService.createCategory(category);
        

    }

    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user,BindingResult bindingResult){
    
       return getService.createUser(user, bindingResult);
        
    }   

    @PutMapping("/sell")
    public ResponseEntity<?> createOrder(@RequestBody Orderdto orderdto) {
        return getService.createOrder(orderdto);
    }

    @GetMapping("/filterproduct")
    public ResponseEntity<?> getProduct(
        @RequestParam(required = false) Integer product_id,
        @RequestParam(required = false) Integer category_id) {
      
        return getService.getProduct(product_id, category_id);
    }

    @GetMapping("/filtercategory")
    public ResponseEntity<?> getCategory(
        @RequestParam (required = false) Integer category_id){

           return getService.getCategory(category_id);
            
        }

        @DeleteMapping("/deleteCategory")
        public ResponseEntity<ResponseMessage> deleteCategory(@RequestParam Integer categoryId) {
            return getService.deleteCategory(categoryId);
        }

        @DeleteMapping("/deleteProduct")
        public ResponseEntity<ResponseMessage> deleteProduct(@RequestParam Integer productId) {
            return getService.deleteProduct(productId);
        }

        @PutMapping("/updateCategory")
        public ResponseEntity<?> updateCategory(@RequestParam Integer categoryId,
            @RequestParam String categoryName){
            
                return getService.updateCategory(categoryId, categoryName);
            
        }

        @PutMapping("/updateProduct")
        public ResponseEntity<?> updateCategory(@RequestParam Integer productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer quantity

            ){
            return getService.updateProduct(productId, productName, categoryId, price, quantity);
            
        }



    @PutMapping("/restock")
    public ResponseEntity<?> restock(@RequestBody Orderdto orderdto) {
    return getService.restock(orderdto);
}

}
