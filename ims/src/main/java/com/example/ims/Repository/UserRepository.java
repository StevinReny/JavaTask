package com.example.ims.Repository;

// import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// import com.example.ims.Module.Products;
import com.example.ims.Module.User;

public interface UserRepository extends JpaRepository<User,Integer> {

    // @Query("SELECT u FROM User u WHERE p.username = :username")
    // Optional<User>findByUserName(@Param("username") String username);
    
    @Query("SELECT u.role FROM User u WHERE u.id = :userId")
    String findUserRoleByUserId(@Param("userId") int userId);


}
