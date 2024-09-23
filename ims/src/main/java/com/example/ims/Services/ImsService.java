package com.example.ims.Services;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

import com.example.ims.Module.Category;
import com.example.ims.Module.Order;
import com.example.ims.Module.Orderdto;
import com.example.ims.Module.Productdto;
import com.example.ims.Module.Products;
import com.example.ims.Module.ResponseMessage;
import com.example.ims.Module.User;
import com.example.ims.Repository.CategoryRepository;
import com.example.ims.Repository.OrderRepository;
import com.example.ims.Repository.ProductRepository;
import com.example.ims.Repository.UserRepository;

// import io.micrometer.core.ipc.http.HttpSender.Response;

@Service
public class ImsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired 
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public ResponseEntity<?> formatResponse(Integer product_id,Integer category_id){
        
        if(product_id==null&&category_id==null){
            return ResponseEntity.ok(productRepository.findAll());
        }
        else if(product_id!=null&&category_id!=null){
            return ResponseEntity.badRequest().body(Map.of("message","Not allowed to enter both"));
        }
        else if(category_id!=null){
            List<Products>temp= productRepository.findByCategory_id(category_id);
            if(!temp.isEmpty()){
                return ResponseEntity.ok(temp);
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Category id not found"));
            }
        }
        else if (product_id!=null){
           Optional<Products> temp=productRepository.findById(product_id);
           if(temp.isPresent()){
            return ResponseEntity.ok(temp);
           }
           else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Product id not found"));
           }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","An error occured"));
        }
        
        

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
        try{

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
        catch (DataIntegrityViolationException e) {  
            ResponseMessage responseMessage = new ResponseMessage("Category name already exist");        
            return ResponseEntity.badRequest().body(responseMessage);
        }

    }

    public ResponseEntity<ResponseMessage> updateProduct(Integer productId, String productName, Integer categoryId,
            Double price, Integer quantity) {
        try{

            if (productRepository.existsById(productId)) {
                if(productName == null && categoryId == null && price == null && quantity == null){
                    return ResponseEntity.badRequest()
                                .body(new ResponseMessage("Nothing to be updated"));
                }
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
        catch (DataIntegrityViolationException e) {  
            ResponseMessage responseMessage = new ResponseMessage("Product name already exist");        
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }


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


    public boolean validate1(Products product,Orderdto orderdto){
        
        if(orderdto.getQuantity()<=0){ 
            return false;    
        }
        else{
            int val=product.getQuantity()+orderdto.getQuantity();
            product.setQuantity(val);
            return true;
        }
    }

    public ResponseEntity<?> createProduct(Productdto product,BindingResult bindingResult){
        Category category = categoryRepository.findById(product.getCategory_id()).orElse(null);
        if(category==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Category not found"));
        }

        Optional<Products> existingProduct = productRepository.findByProductName(product.getProduct_name());
        
        if (existingProduct.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Product name already exists"));
        }
        List<String> errors= new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList()));
            
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message",String.join(", ", errors)));
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

    public ResponseEntity<?> createCategory(Category category){
        Optional<Category> existingCategory = categoryRepository.findByProductName(category.getCategory_name());
        
        if (existingCategory.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Category name already exists"));
        }
        categoryRepository.save(category);

        return ResponseEntity.ok().body(Map.of("message","Successfully inserted"));

    }

    public ResponseEntity<?> createUser(User user,BindingResult bindingResult){
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

    public ResponseEntity<?> createOrder(Orderdto orderdto){
        Products product = productRepository.findById(orderdto.getProduct_id()).orElse(null);
        User user= userRepository.findById(orderdto.getUserid()).orElse(null);

        if(user==null){
            return ResponseEntity.ok(Map.of("message"," User not found"));
        }
        if(product==null){
            return ResponseEntity.ok(Map.of("message"," Product not found"));
        }
        if(user.getRole().equalsIgnoreCase("buyer")){

            if(validate(product, orderdto)){
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
                "message", "No sufficient quantity. Available quantity is "+product.getQuantity()
            ));
            }
        }
        else{
            return ResponseEntity.badRequest().body(Map.of(
            "message", "No access for you to buy"
        ));
        }
    }


    public ResponseEntity<?> getCategory(Integer category_id){
        Object responseBody=formatCategoryResponse(category_id);
        if(responseBody==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Category not Found"));
        }
        else{
            return ResponseEntity.ok(responseBody);
        }
    }

    public ResponseEntity<?> restock(Orderdto orderdto){
        
        Products product = productRepository.findById(orderdto.getProduct_id()).orElse(null);
        User user= userRepository.findById(orderdto.getUserid()).orElse(null);
        
        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message"," User not found"));
        }
        if(product==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message"," Product not found"));
        }
        // System.out.println(user.getRole());
        if(user.getRole().equalsIgnoreCase("seller")){
            if(validate1(product, orderdto)){
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
                return ResponseEntity.badRequest().body(Map.of(
            "message", "Invalid quantity to restock"
        ));
            }
        }
        
        else{
            return ResponseEntity.badRequest().body(Map.of(
            "message", "No access to restock"
        ));
        }


    }

}

