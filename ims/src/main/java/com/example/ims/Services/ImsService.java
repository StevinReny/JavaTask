package com.example.ims.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    private ConcurrentHashMap<Integer, Products> productCache = new ConcurrentHashMap<>();  
    private ConcurrentHashMap<Integer, Category> categoryCache = new ConcurrentHashMap<>();

    // Create Product
    public ResponseEntity<?> createProduct(Productdto product){
        //Check whether the category is found or not
        Category category = categoryRepository.findById(product.getCategory_id()).orElse(null);
        if(category == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Category not found"));
        }

        Products products=new Products();
        products.setProduct_name(product.getProduct_name());
        products.setCategory(category);
        products.setPrice(product.getPrice());
        products.setQuantity(product.getQuantity());

        productRepository.save(products);
        productCache.put(products.getProduct_id(), products);
        return ResponseEntity.ok().body(Map.of("message", "Product created successfully"));
    }

    // Create Category
    public ResponseEntity<?> createCategory(Category category){
        
        categoryRepository.save(category);
        categoryCache.put(category.getCategory_id(), category);
        return ResponseEntity.ok().body(Map.of("message","Category created successfully"));

    }

    //Create User
    public ResponseEntity<?> createUser(User user){
       
        try{
            userRepository.save(user);
            return ResponseEntity.ok().body(Map.of("message","User created successfully"));
        }
        catch (DataIntegrityViolationException e) {            
                return ResponseEntity.badRequest().body(Map.of("message","User with the same username and role already exists")); }
    }

    //Get Product - by productId, categoryId
    public ResponseEntity<?> getProduct(Integer product_id,Integer category_id){

        if(product_id==null && category_id==null){
            return ResponseEntity.ok(productRepository.findAll());
        }

        else if(product_id!=null && category_id!=null){
            return ResponseEntity.badRequest().body(Map.of("message","Not allowed to enter both"));
        }

        else if(category_id!=null){
            if(categoryRepository.existsById(category_id)){
                List<Products> products= productRepository.findByCategory_id(category_id);
                if(!products.isEmpty()){
                    return ResponseEntity.ok(products);
                }
                else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","No product under the category"));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Category id not found"));
            }
        }
        else if (product_id!=null){
            if(productCache.get(product_id)==null){
                Optional<Products> product=productRepository.findById(product_id);
                if(product.isPresent()){
                    productCache.put(product.get().getProduct_id(), product.get());
                    return ResponseEntity.ok(product);
                }
                else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Product id not found"));
                }
            }
            else{
                return ResponseEntity.ok(productCache.get(product_id));
           }
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","An error occured"));
        }
    }

    // Get Category - by categoryId
    public ResponseEntity<?> getCategory(Integer category_id){
        if(category_id==null){
            return ResponseEntity.ok(categoryRepository.findAll());
        }
        if(categoryCache.get(category_id)==null)
        {
            Optional<Category> category= categoryRepository.findById(category_id);
            if(category.isPresent()){
                categoryCache.put(category.get().getCategory_id(),category.get());
                return ResponseEntity.ok(category);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","The category id not found"));
        }
        else{
            return ResponseEntity.ok(Map.of("message",categoryCache.get(category_id)));
        }
    }

    //Delete Category
    public ResponseEntity<ResponseMessage> deleteCategory(Integer category_id) {
        Optional<Category> category = categoryRepository.findById(category_id);
        
        if (category.isPresent()) {
            Category categoryDetails = category.get();
            // Check if there are products under the category
            if (productRepository.findByCategory_id(category_id).isEmpty()) {
                categoryRepository.deleteById(category_id);
                categoryCache.remove(category_id);
                ResponseMessage responseMessage = new ResponseMessage("Successfully deleted " + categoryDetails.getCategory_name() + " from the Inventory management system");
                return ResponseEntity.ok(responseMessage);
            } else {
                ResponseMessage responseMessage = new ResponseMessage("Cannot delete " + categoryDetails.getCategory_name() + " because it has products under it");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
            }
        } else {
            ResponseMessage responseMessage = new ResponseMessage("Category id not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }
    }
    

    //Delete Product
    public ResponseEntity<ResponseMessage> deleteProduct(Integer productId) {
            Optional<Products> product=productRepository.findById(productId);
            if(product.isPresent()){
                productRepository.deleteById(productId);
                productCache.remove(productId);
                ResponseMessage responseMessage=new ResponseMessage("Successfully deleted " +product.get().getProduct_name()+ " from the Inventory management system");
                return ResponseEntity.ok(responseMessage);
            }
            else{
                ResponseMessage responseMessage=new ResponseMessage("Product id not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
    }

    //Update Category
    public ResponseEntity<ResponseMessage> updateCategory(Integer category_id,String category_name) {
        Optional<Category> category=categoryRepository.findById(category_id);
        if(category.isPresent()){
            category.get().setCategory_name(category_name);
            categoryRepository.save(category.get());
            categoryCache.put(category.get().getCategory_id(),category.get());
            ResponseMessage responseMessage=new ResponseMessage("Category updated successfully");
            return ResponseEntity.ok(responseMessage);
        }
        else{
            ResponseMessage responseMessage=new ResponseMessage("Category id not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }       
    }

    //Update Product
    public ResponseEntity<ResponseMessage> updateProduct(Integer productId, String productName, Integer categoryId,
            Double price, Integer quantity) {
        if(productName == null && categoryId == null && price == null && quantity == null){
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Nothing to be updated"));
            }
            
        Optional<Products> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            if (productName != null) {
                product.setProduct_name(productName);;
            }
            if (categoryId != null) {
                Optional<Category> category = categoryRepository.findById(categoryId);
                if (category.isPresent()) {
                    product.setCategory(category.get());
                } 
                else {
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

            productRepository.save(product);
            productCache.put(product.getProduct_id(), product);
            
            return ResponseEntity.ok(new ResponseMessage("Product updated successfully"));
        } 
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Product not found"));
        }
        
    }

    //Sell
    public ResponseEntity<?> sell(Orderdto orderdto){
        
        Products product = productRepository.findById(orderdto.getProduct_id()).orElse(null);
        User user= userRepository.findById(orderdto.getUserid()).orElse(null);

        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","User not found"));
        }
        if(product==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Product not found"));
        }

        if(user.getRole().equalsIgnoreCase("buyer")){
            if(validate(product, orderdto)){
                Order order=new Order();
                order.setUser(user);
                order.setProduct(product);
                order.setQuantity(orderdto.getQuantity());    
                orderRepository.save(order);
                productCache.put(product.getProduct_id(), product);
                return ResponseEntity.ok().body(Map.of(
                "message", "Successfully sold out the product with id: "+product.getProduct_id()));
            }
            else{
                return ResponseEntity.ok().body(Map.of(
                "message", "No sufficient quantity. Available quantity is "+product.getQuantity()));
            }
        }
        else{
            return ResponseEntity.badRequest().body(Map.of(
            "message", "No access for you to buy"));
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

    //Restock
    public ResponseEntity<?> restock(Orderdto orderdto){
        
        Products product = productRepository.findById(orderdto.getProduct_id()).orElse(null);

        User user= userRepository.findById(orderdto.getUserid()).orElse(null);
        
        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message"," User not found"));
        }

        if(product==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message"," Product not found"));
        }

        if(user.getRole().equalsIgnoreCase("seller")){
            Order order=new Order();
            order.setUser(user);
            order.setProduct(product);
            order.setQuantity(orderdto.getQuantity());
            orderRepository.save(order);
            product.setQuantity(orderdto.getQuantity()+product.getQuantity());
            productRepository.save(product);
            productCache.put(product.getProduct_id(), product);
            return ResponseEntity.ok().body(Map.of("message", "Successfully restocked the product with id: "+product.getProduct_id()));
        }
        
        else {
            return ResponseEntity.badRequest().body(Map.of("message", "No access to restock"));
        }
        
    }

}

