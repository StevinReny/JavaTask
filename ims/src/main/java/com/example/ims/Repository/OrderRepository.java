package com.example.ims.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ims.Module.Order;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    
}
