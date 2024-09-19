package com.example.ims.Repository;

import java.util.List;
// import java.util.Optional;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ims.Module.Category;

@Repository
public interface CategoryRepository extends JpaRepository <Category,Integer> {
    
    List<Category> findAll();
    @Query("SELECT p FROM Category p WHERE p.category_name = :productname")
    Optional<Category>findByProductName(@Param("productname") String productname);


    
} 