package com.example.ims.Controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

// import java.util.Optional;
import java.util.Optional;

// import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.example.ims.Module.Order;
import com.example.ims.Module.Orderdto;
import com.example.ims.Module.Productdto;
import com.example.ims.Module.Products;

import com.example.ims.Module.User;

import com.example.ims.Module.ResponseMessage;

import com.example.ims.Repository.CategoryRepository;
import com.example.ims.Repository.OrderRepository;
import com.example.ims.Repository.ProductRepository;
import com.example.ims.Repository.UserRepository;
import com.example.ims.Services.ImsService;
import java.util.stream.Collectors;
// import com.example.ims.Services.StockValidate;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ims")
public class ImsController {
    
    @Autowired
    private ImsService getService;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ImsController(ProductRepository productRepository, CategoryRepository categoryRepository,UserRepository userRepository,OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository=userRepository;
        this.orderRepository=orderRepository;
    }
    

    @PostMapping("/createproduct")
    public ResponseEntity<?> createProduct(@RequestBody Productdto product) {
    
        Category category = categoryRepository.findById(product.getCategory_id()).orElse(null);
        if(category==null){
            return ResponseEntity.ok(Map.of("message","Category not found"));
        }

        Optional<Products> existingProduct = productRepository.findByProductName(product.getProduct_name());
        
        if (existingProduct.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Product name already exists"));
        }
        
        
        Products products=new Products();
        products.setProduct_name(product.getProduct_name());
        products.setCategory(category);
        products.setPrice(product.getPrice());
        products.setQuantity(product.getQuantity());

        productRepository.save(products);

        return ResponseEntity.ok().body(Map.of(
            "message", "Product successfully created"
        ));
    }

    @PostMapping("/createcategory")
    public ResponseEntity<?> createcategory(@RequestBody Category category){

        Optional<Category> existingCategory = categoryRepository.findByProductName(category.getCategory_name());
        
        if (existingCategory.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Category name already exists"));
        }
        categoryRepository.save(category);

        return ResponseEntity.ok().body(Map.of("message","Successfully inserted"));

    }

    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user,BindingResult bindingResult){
    
        // Optional<User> existingUser = userRepository.findByUserName(user.getUsername());
        // if(existingUser.isPresent()){
        //     return ResponseEntity.badRequest().body(Map.of("message","User name already exists"));
        // }
        List<String> errors = new ArrayList<>();
        try{
            if (bindingResult.hasErrors()) {
                errors.addAll(bindingResult.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList()));
                
            }
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message",String.join(",", errors)));
            }

            userRepository.save(user);
    
            return ResponseEntity.ok().body(Map.of("message","Successfully inserted"));
        }
        catch (DataIntegrityViolationException e) {            
                return ResponseEntity.badRequest().body(Map.of("message","Product with the same username and role already exists")); }
    }   

    @PutMapping("/sell")
    public ResponseEntity<?> createOrder(@RequestBody Orderdto orderdto) {
    
        Products product = productRepository.findById(orderdto.getProduct_id()).orElse(null);
        User user= userRepository.findById(orderdto.getUserid()).orElse(null);

        if(user==null){
            return ResponseEntity.ok(Map.of("message"," User not found"));
        }
        if(product==null){
            return ResponseEntity.ok(Map.of("message"," Product not found"));
        }
        if(user.getRole().equals("buy")){

            if(getService.validate(product, orderdto)){
                Order order=new Order();
                order.setUser(user);
                order.setProduct(product);
                order.setQuantity(orderdto.getQuantity());
    
                orderRepository.save(order);
    
            return ResponseEntity.ok().body(Map.of(
                "message", "Order successfully created"
            ));
            }
            else{
                return ResponseEntity.ok().body(Map.of(
                "message", "No sufficient quantity"
            ));
            }
        }
        else{
            return ResponseEntity.ok().body(Map.of(
            "message", "No access for you to buy"
        ));
        }

        
        
        
    }

    @GetMapping("/filterproduct")
    public ResponseEntity<?> getProduct(
        @RequestParam(required = false) Integer product_id,
        @RequestParam(required = false) Integer category_id) {
      
        Object responseBody =getService.formatResponse(product_id, category_id);
        
        if(responseBody==null){
            return ResponseEntity.ok().body(Map.of("message","Not allowed to enter both"));
        }
        else{
            return ResponseEntity.ok(responseBody);
        }
    }

    @GetMapping("/filtercategory")
    public ResponseEntity<?> getCategory(
        @RequestParam (required = false) Integer category_id){

            Object responseBody=getService.formatCategoryResponse(category_id);
            if(responseBody==null){
                return ResponseEntity.ok().body(Map.of("message","Not Found"));
            }
            else{
                return ResponseEntity.ok(responseBody);
            }
            
        }







    

        @DeleteMapping("/deleteCategory")
        public ResponseEntity<ResponseMessage> deleteCategory(@RequestParam Integer categoryId) {
            return getService.deleteCategory(categoryId);
        }

        @DeleteMapping("/deleteProduct")
        public ResponseEntity<ResponseMessage> deleteProduct(@RequestParam Integer productId) {
            return getService.deleteProduct(productId);
        }



    @PutMapping("/restock")
    public ResponseEntity<?> restock(@RequestBody Orderdto orderdto) {
    
        Products product = productRepository.findById(orderdto.getProduct_id()).orElse(null);
        User user= userRepository.findById(orderdto.getUserid()).orElse(null);
        
        if(user==null){
            return ResponseEntity.ok(Map.of("message"," User not found"));
        }
        if(product==null){
            return ResponseEntity.ok(Map.of("message"," Product not found"));
        }
        // System.out.println(user.getRole());
        if(user.getRole().equals("sell")){
            if(getService.validate1(product, orderdto)){
                Order order=new Order();
                order.setUser(user);
                order.setProduct(product);
                order.setQuantity(orderdto.getQuantity());
    
                orderRepository.save(order);
    
                return ResponseEntity.ok().body(Map.of(
                    "message", "Order successfully created"
                ));
            }
            else{
                return ResponseEntity.ok().body(Map.of(
            "message", "No sufficient quantity to restock"
        ));
            }
        }
        
        else{
            return ResponseEntity.ok().body(Map.of(
            "message", "No access to restock"
        ));
        }


    }
}
