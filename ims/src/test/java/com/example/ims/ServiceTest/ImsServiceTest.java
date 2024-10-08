package com.example.ims.ServiceTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.concurrent.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

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
import com.example.ims.Services.ImsService;




public class ImsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ConcurrentHashMap<Integer, Products> productCache;

    @Mock
    private ConcurrentHashMap<Integer, Category> categoryCache;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ResponseMessage responseMessage;

    @InjectMocks
    private ImsService imsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateProductSucess(){

        Productdto productdto=new Productdto();
        productdto.setProduct_name("Bread");
        productdto.setCategory_id(1);
        productdto.setPrice(29.7);
        productdto.setQuantity(10);

        Category category=new Category();
        category.setCategory_name("Grocery");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.findByProductName("Bread")).thenReturn(Optional.empty());
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response= imsService.createProduct(productdto, bindingResult);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Product created successfully", ((Map<?,?>)response.getBody()).get("message"));
        verify(productRepository,times(1)).save(any(Products.class));
    }


    @Test
    public void Createproduct_CategoryNotFound(){
        Productdto productdto = new Productdto();
        productdto.setCategory_id(1);
        productdto.setPrice(29.9);
        productdto.setQuantity(10);
        productdto.setProduct_name("Bread");

        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        
        ResponseEntity<?> response= imsService.createProduct(productdto, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", ((Map<?,?>)response.getBody()).get("message"));

    }

    @Test
    public void testCreateProduct_ProductNameexist(){

        Productdto productdto=new Productdto();
        productdto.setCategory_id(1);
        productdto.setPrice(29.9);
        productdto.setQuantity(10);
        productdto.setProduct_name("Bread");

        Category category=new Category();
        Products products=new Products();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.findByProductName("Bread")).thenReturn(Optional.of(products));

        ResponseEntity<?> responseEntity= imsService.createProduct(productdto, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Product name already exists", ((Map<?,?>)responseEntity.getBody()).get("message"));
    }   



    @Test
    public void testCreateCategory_Success() {

        Category category= new Category();

        when(categoryRepository.findByProductName("Bread")).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity=imsService.createCategory(category);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Map.of("message","Category created successfully"), responseEntity.getBody());
        verify(categoryRepository,times(1)).save(any(Category.class));
       
    }

    @Test
    public void testCreateCategory_AlreadyExists() {
        Category category= new Category();
       category.setCategory_name("Bread");

       when(categoryRepository.findByProductName("Bread")).thenReturn(Optional.of(category));
       
       ResponseEntity<?> responseEntity=imsService.createCategory(category);
       assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
       assertEquals(Map.of("message","Category name already exists"), ((Map<?,?>)responseEntity.getBody()));
      
    }
    @Test
    public void testCreateUser_Sucess(){
        User user=new User();
        
        when(bindingResult.hasErrors()).thenReturn(false);
        
        ResponseEntity <?> responseEntity=imsService.createUser(user,bindingResult);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User created successfully",((Map<?,?>) responseEntity.getBody()).get("message"));
        verify(userRepository,timeout(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_ValidationErrors() {
        // Arrange
        User user = new User();

        ObjectError error = new ObjectError("role", "Invalid role");
        List<ObjectError> validationErrors = Collections.singletonList(error);

        when(bindingResult.hasErrors()).thenReturn(true); 
        when(bindingResult.getAllErrors()).thenReturn(validationErrors);

        ResponseEntity<?> response = imsService.createUser(user, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid role", ((Map<?, ?>) response.getBody()).get("message"));
        verify(userRepository, never()).save(any(User.class)); 
    }

    @Test
    public void testCreateUser_DuplicateUser() {
        User user = new User();
        user.setUsername("John");
        user.setRole("buyer");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("Duplicate")).when(userRepository).save(user);

        ResponseEntity<?> response = imsService.createUser(user, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User with the same username and role already exists", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    public void testCreateOrder_noUser(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        Products products=new Products();
        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response=imsService.sell(orderdto);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", ((Map<?,?>)response.getBody()).get("message"));
        verify(productRepository,times(1)).findById(anyInt());

    }
    @Test
    public void testCreateOrder_noProduct(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        User user=new User();

        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ResponseEntity<?> response=imsService.sell(orderdto);
        assertEquals("Product not found", ((Map<?,?>)response.getBody()).get("message"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
    @Test
    public void testCreateOrder_noAccess() {

        Orderdto orderdto = new Orderdto();
        orderdto.setUserid(1);
        orderdto.setProduct_id(2);

        Products products=new Products();

        User user=new User();
        user.setRole("seller");
        when(productRepository.findById(2)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = imsService.sell(orderdto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No access for you to buy", ((Map<?, ?>) response.getBody()).get("message"));
        verify(orderRepository,never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_insufficient(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        orderdto.setQuantity(15);

        Products products=new Products();
        products.setQuantity(10);

        User user=new User();
        user.setRole("buyer");
        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        // when(imsService.validate(products, orderdto)).thenReturn(false);

        ResponseEntity<?> responseEntity=imsService.sell(orderdto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("No sufficient quantity. Available quantity is 10", ((Map<?,?>)responseEntity.getBody()).get("message"));

    }

    @Test
    public void testCreateOrder_success(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        orderdto.setQuantity(5);

        User user=new User();
        user.setRole("buyer");

        Products products=new Products();
        products.setQuantity(10);

        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        // when(imsService.validate(products, orderdto)).thenReturn(true);

        ResponseEntity<?> responseEntity=imsService.sell(orderdto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successfully sold out the product with id: " +products.getProduct_id(), ((Map<?,?>)responseEntity.getBody()).get("message"));
        verify(orderRepository,times(1)).save(any(Order.class));
    }

    @Test
    public void testrestock_noUser(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        Products products=new Products();

        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response=imsService.restock(orderdto);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(" User not found", ((Map<?,?>)response.getBody()).get("message"));
        verify(productRepository,times(1)).findById(anyInt());

    }
    @Test
    public void testrestock_noProduct(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        User user=new User();

        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ResponseEntity<?> response=imsService.restock(orderdto);
        assertEquals(" Product not found", ((Map<?,?>)response.getBody()).get("message"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
    @Test
    public void testrestock_noAccess() {

        Orderdto orderdto = new Orderdto();
        orderdto.setUserid(1);
        orderdto.setProduct_id(2);

        Products products=new Products();

        User user=new User();
        user.setRole("buyer");

        when(productRepository.findById(2)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = imsService.restock(orderdto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No access to restock", ((Map<?, ?>) response.getBody()).get("message"));
        verify(orderRepository,never()).save(any(Order.class));
    }


    @Test
    public void testrestock_success(){
        Orderdto orderdto=new Orderdto();
        orderdto.setProduct_id(1);
        orderdto.setUserid(1);
        orderdto.setQuantity(5);

        User user=new User();
        user.setRole("sEller");

        Products products=new Products();
        products.setQuantity(10);

        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        // when(imsService.validate(products, orderdto)).thenReturn(true);

        ResponseEntity<?> responseEntity=imsService.restock(orderdto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successfully restocked the product with id: "+products.getProduct_id(), ((Map<?,?>)responseEntity.getBody()).get("message"));
        verify(orderRepository,times(1)).save(any(Order.class));
    }

    @Test
    public void testgetproduct_noids(){
        List<Products>productList=new ArrayList<>();
        Products products=new Products();
        products.setProduct_id(1);
        products.setProduct_name("Ice");
        products.setPrice(10.0);
        products.setQuantity(10);
        productList.add(products);

        when(productRepository.findAll()).thenReturn(productList);
        ResponseEntity<?> responseEntity=imsService.getProduct(null, null);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productList, responseEntity.getBody());


    }
    @Test
    public void testgetproduct_idsgiven(){
        ResponseEntity<?> responseEntity=imsService.getProduct(1, 1);

        assertEquals("Not allowed to enter both", ((Map<?,?>)responseEntity.getBody()).get("message"));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testgetproduct_categoryidGivencategoryNotFound(){
        when(categoryRepository.existsById(1)).thenReturn(false);
        ResponseEntity<?> responseEntity=imsService.getProduct(null, 1);
        assertEquals(Map.of("message","Category id not found"),responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }

    @Test
    public void testgetproduct_categoryidGivenproductNotFound(){
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(productRepository.findByCategory_id(1)).thenReturn(Collections.emptyList());
        ResponseEntity<?> responseEntity=imsService.getProduct(null, 1);
        assertEquals(Map.of("message","No product under the category"),responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }

    @Test
    public void testgetproduct_categoryidGivensucess(){
        Category category=new Category();
        category.setCategory_id(1);
        Products products=new Products();
        products.setProduct_name("Ice");
        products.setCategory(category);
        products.setPrice(10);
        products.setQuantity(10);

        when(categoryRepository.existsById(1)).thenReturn(true);
        when(productRepository.findByCategory_id(1)).thenReturn(List.of(products));
        ResponseEntity<?> responseEntity=imsService.getProduct(null,1);
        assertEquals(List.of(products), responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

}

    @Test
    public void testgetproduct_productidgivenfromcache(){
        Products products=new Products();
        products.setProduct_id(1);
        when(productCache.get(1)).thenReturn(products);

        ResponseEntity<?> responseEntity = imsService.getProduct(1, null);
        assertEquals(products, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(productRepository,never()).findById(anyInt());
        

    }

    @Test
    public void testgetproduct_productidgivenfromrepo(){
        Products products=new Products();
        products.setProduct_id(1);
        products.setProduct_name("Ice");
        when(productCache.get(1)).thenReturn(null);
        when(productRepository.findById(1)).thenReturn(Optional.of(products));

        ResponseEntity<?> responseEntity=imsService.getProduct(1, null);
        assertEquals(Optional.of(products), responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(productRepository,times(1)).findById(1);
        verify(productCache,times(1)).put(1, products);
    }

    @Test
    public void testgetproduct_productidGivennoxtFound(){
        when(productCache.get(1)).thenReturn(null);
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity=imsService.getProduct(1, null);

        assertEquals(Map.of("message","Product id not found"), responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testGetCategory_cacheFound(){
        Category category=new Category();
        category.setCategory_id(1);
        when(categoryCache.get(1)).thenReturn(category);

        ResponseEntity<?> responseEntity=imsService.getCategory(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Map.of("message",category), responseEntity.getBody());
    }

    @Test
    public void testGetCategory_categoryIdnull(){
        Category category=new Category();
        category.setCategory_id(2);
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        ResponseEntity<?> responseEntity=imsService.getCategory(null);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(List.of(category), responseEntity.getBody());
    }
    
    @Test
    public void testGetCategory_cacheNotFoundSucess(){
        Category category=new Category();
        category.setCategory_id(1);
        when(categoryCache.get(1)).thenReturn(null);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        ResponseEntity<?> responseEntity=imsService.getCategory(1);
        assertEquals(Optional.of(category), responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(categoryRepository,times(1)).findById(1);
        verify(categoryCache,times(1)).put(1, category);
    }

    @Test
    public void testGetCategory_categoryidNotFound(){
        when(categoryCache.get(1)).thenReturn(null);
        when(categoryRepository.findAll()).thenReturn(List.of());
        ResponseEntity<?> responseEntity=imsService.getCategory(1);
        assertEquals(Map.of("message","The category id not found"), responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteCategory_Success(){
        Category category=new Category();
        category.setCategory_id(1);
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.findByCategory_id(1)).thenReturn(List.of());
        ResponseEntity<?> responseEntity=imsService.deleteCategory(1);
        ResponseMessage responseMessage= new ResponseMessage("Successfully deleted "+category.getCategory_name()+" from the Inventory management system");
        assertEquals(responseMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(categoryRepository,times(1)).deleteById(1);
        verify(categoryCache,times(1)).remove(1);

    }

    @Test
    public void testDeleteCategory_productFound(){
       
        Category category=new Category();
        category.setCategory_id(1);
        Products products=new Products();
        products.setCategory(category);
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.findByCategory_id(1)).thenReturn(List.of(products));

        ResponseEntity<?> responseEntity=imsService.deleteCategory(1);
        ResponseMessage responseMessage=new ResponseMessage("Cannot delete " +category.getCategory_name()+ " because it has products under it");
        assertEquals(responseMessage,responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(categoryRepository,times(1)).findById(1);
        verify(productRepository,times(1)).findByCategory_id(1);
        verify(categoryRepository,times(0)).deleteById(1);
    }

    @Test
    public void testDeleteCategory_categoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ResponseMessage> responseEntity = imsService.deleteCategory(1);
        
        // Assert
        ResponseMessage expectedResponseMessage = new ResponseMessage("Category id not found");
        assertEquals(expectedResponseMessage.getMessage(), ((ResponseMessage) responseEntity.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        
        // Verify interactions
        verify(categoryRepository, times(1)).findById(1);
        verify(productRepository, times(0)).findByCategory_id(1);
    }


    @Test
    public void testDeleteProduct_NotFound(){
        when(productRepository.existsById(1)).thenReturn(false);

        ResponseMessage responseMessage=new ResponseMessage("Product id not found");
        ResponseEntity<?> responseEntity=imsService.deleteProduct(1);

        assertEquals(responseMessage, responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        // verify(productRepository,times(1)).existsById(1);
        verify(productRepository,times(1)).findById(1);

    }

    @Test
    public void testDeleteProduct_Success(){
        Products products=new Products();
        products.setProduct_id(1);
        // when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(products));

        ResponseMessage responseMessage=new ResponseMessage("Successfully deleted " +products.getProduct_name()+ " from the Inventory management system");
        ResponseEntity<?> responseEntity=imsService.deleteProduct(1);
        assertEquals(responseMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // verify(productRepository,times(1)).existsById(1);
        verify(productRepository,times(1)).findById(1);
        verify(productRepository,times(1)).deleteById(1);
        verify(productCache,times(1)).remove(1);

    }

    @Test
    public void testDeleteProduct_NottFound(){
        // when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        ResponseMessage responseMessage=new ResponseMessage("Product id not found");
        ResponseEntity<?> responseEntity=imsService.deleteProduct(1);

        assertEquals(responseMessage, responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        // verify(productRepository,times(1)).existsById(1);
        verify(productRepository,times(1)).findById(1);

    }

    
    @Test
    public void updateProduct_noUpdation(){
        // when(productRepository.existsById(1)).thenReturn(true);

        ResponseEntity<?> responseEntity=imsService.updateProduct(1, null, null, null, null);
        ResponseMessage responseMessage=new ResponseMessage("Nothing to be updated");
        assertEquals(responseEntity.getBody(), responseMessage);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        // verify(productRepository,times(1)).existsById(1);
    }


    @Test
    public void updateProduct_categoryNotFound(){
        Products products=new Products();
        products.setProduct_id(1);
        // when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        when(categoryRepository.findById(2)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity=imsService.updateProduct(1, null, 2, null, null);
        ResponseMessage responseMessage=new ResponseMessage("Category not found");


        assertEquals(responseEntity.getBody(), responseMessage);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateProduct_Sucess(){
        Products products=new Products();
        products.setProduct_id(1);
        // when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(products));
        
        ResponseEntity<?> responseEntity=imsService.updateProduct(1, "Test product name", null, null, null);
        ResponseMessage responseMessage=new ResponseMessage("Product updated successfully");

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), responseMessage);
        verify(productRepository,times(1)).save(any(Products.class));
        verify(productCache,times(1)).put(1, products);


    } 

    @Test
    public void testUpdateProduct_Notfound(){
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity=imsService.updateProduct(1, "Product", null, null, null);
        ResponseMessage responseMessage=new ResponseMessage("Product not found");

        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(responseEntity.getBody(), responseMessage);
        verify(productRepository,never()).save(any(Products.class));


    }

    @Test
    public void testUpdateCategory_NotFound(){
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity =imsService.updateCategory(1, null);
        ResponseMessage responseMessage=new ResponseMessage("Category id not found");

        assertEquals(responseEntity.getBody(), responseMessage);
        assertEquals(responseEntity.getStatusCode(),HttpStatus.NOT_FOUND);
    }


    @Test
    public void testUpdateCategory_Success(){
        Category category=new Category();
        category.setCategory_id(1);
        // when(categoryRepository.existsById(1)).thenReturn(true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        ResponseMessage responseMessage=new ResponseMessage("Category updated successfully");
        ResponseEntity<?> responseEntity=imsService.updateCategory(1,"CategoryNew");
        assertEquals(responseMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(categoryRepository,times(1)).save(any(Category.class));
        verify(categoryCache,times(1)).put(1, category);

    }

}
