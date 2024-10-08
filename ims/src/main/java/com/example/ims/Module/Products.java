package com.example.ims.Module;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer product_id;

    @NotBlank(message="Product name should not be blank")
    @Column(unique = true)
    private String product_name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Min(value = 1,message = "Minimum value of price is 1")
    private double price;

    @Min(value = 1,message = "Minimum value of quantity is 1")
    private int quantity;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "product",cascade = CascadeType.REMOVE,orphanRemoval = true)
    @JsonIgnore
    private List<Order> orderTables;
}
