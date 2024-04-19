package com.giovaniwahl.dscatalog;

import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.entities.Category;
import com.giovaniwahl.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {
    public static Product createProduct(){
        Product product = new Product(1L, "Phone", "Good Phone", 800.0,
                "https://img.com/img.ping", Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(2L,"Electronics"));
        return product;
    }
    public static ProductDTO createProductDTO(){
        ProductDTO dto = new ProductDTO(1L, "Phone", "Good Phone", 800.0,
                "https://img.com/img.ping", Instant.parse("2020-10-20T03:00:00Z"));
        return dto;
    }
}
