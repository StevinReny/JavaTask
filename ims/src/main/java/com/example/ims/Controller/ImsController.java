package com.example.ims.Controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
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
    
    private Logger logger = Logger.getLogger(ImsController.class);
    @PostMapping("/createproduct")
    public ResponseEntity<?> createProduct(@RequestBody @Valid Productdto product,BindingResult bindingResult) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<?> response=getService.createProduct(product, bindingResult);
        stopWatch.stop();
        logger.info("Create-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    @PostMapping("/createcategory")
    public ResponseEntity<?> createcategory(@RequestBody Category category){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<?> response=getService.createCategory(category);
        stopWatch.stop();
        logger.info("Create-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user,BindingResult bindingResult){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<?> response=getService.createUser(user, bindingResult);
        stopWatch.stop();
        logger.info("Create-User Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
        
    }   

    @PutMapping("/sell")
    public ResponseEntity<?> createOrder(@RequestBody Orderdto orderdto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<?> response=getService.createOrder(orderdto);
        stopWatch.stop();
        logger.info("Sell Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    @GetMapping("/filterproduct")
    public ResponseEntity<?> getProduct(
        @RequestParam(required = false) Integer product_id,
        @RequestParam(required = false) Integer category_id) {
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<?> response=getService.getProduct(product_id, category_id);
        stopWatch.stop();
        logger.info("Get-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    @GetMapping("/filtercategory")
    public ResponseEntity<?> getCategory(
        @RequestParam (required = false) Integer category_id){
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ResponseEntity<?> response=getService.getCategory(category_id);
            stopWatch.stop();
            logger.info("Get-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
            return response;
        }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<ResponseMessage> deleteCategory(@RequestParam Integer categoryId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<ResponseMessage> response=getService.deleteCategory(categoryId);
        stopWatch.stop();
        logger.info("Delete-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<ResponseMessage> deleteProduct(@RequestParam Integer productId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<ResponseMessage> response=getService.deleteProduct(productId);
        stopWatch.stop();
        logger.info("Delete-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    @PutMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(@RequestParam Integer categoryId,
            @RequestParam String categoryName){
            
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ResponseEntity<ResponseMessage> response=getService.updateCategory(categoryId, categoryName);
            stopWatch.stop();
            logger.info("Update-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
            return response;
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateCategory(@RequestParam Integer productId,
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) Double price,
        @RequestParam(required = false) Integer quantity
        ){
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ResponseEntity<ResponseMessage> response=getService.updateProduct(productId, productName, categoryId, price, quantity);
            stopWatch.stop();
            logger.info("Update-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
            return response;
        }



    @PutMapping("/restock")
    public ResponseEntity<?> restock(@RequestBody Orderdto orderdto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<?> response=getService.restock(orderdto);
        stopWatch.stop();
        logger.info("Restock Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }   

}
