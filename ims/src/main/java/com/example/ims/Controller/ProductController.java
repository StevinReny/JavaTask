package com.example.ims.Controller;

// import java.util.List;
import java.util.Map;
// import java.util.Optional;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ims.Module.Category;
import com.example.ims.Module.Productdto;
import com.example.ims.Module.Products;
import com.example.ims.Module.ResponseMessage;
import com.example.ims.Repository.CategoryRepository;
import com.example.ims.Repository.ProductRepository;
import com.example.ims.Services.GetService;

@RestController
@RequestMapping("/api/ims")
public class ProductController {
    
    @Autowired
    private GetService getService;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
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


}
