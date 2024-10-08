package com.example.ims.Module;

// import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="users",uniqueConstraints = @UniqueConstraint(columnNames = {"username","role"}))
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int userid;

    @Pattern(regexp ="(?i)^(buyer|seller)$",message="Invalid role")
    private String role;

    @NotBlank(message="Username should not be blank")
    private String username;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

