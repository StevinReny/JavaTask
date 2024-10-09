package com.example.ims.Module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductView {

    private Integer product_id;

    private String product_name;

    private Integer category_id;

    private double price;

    private int quantity;

}
