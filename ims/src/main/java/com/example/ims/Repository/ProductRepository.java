package com.example.ims.Repository;

import java.util.List;
// import java.util.Optional;
import java.util.Optional;

// import java.util.List;
// import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ims.Module.Products;
@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {

    @Query("SELECT p FROM Products p WHERE p.product_name = :productname")
    Optional<Products>findByProductName(@Param("productname") String productname);
    // List<Products> findAll();
    // Optional<Products> findByProductName(String productName);


    // @Query("SELECT p from Products p WHERE p.product_id=:productid")
    // List<Products> findById(@Param("productid")Integer product_id);


    @Query("SELECT p from Products p WHERE p.category.category_id=:categoryid")
    List<Products> findByCategory_id(@Param("categoryid")Integer categoryid);



}