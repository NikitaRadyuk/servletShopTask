package com.inno.servlettask.service;

import com.inno.servlettask.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findByPriceLessThan(Integer maxPrice);

    List<Product> findByPriceGreaterThan(Integer minPrice);

    Product createProduct(Product product);

    Product updateProduct(Product product);

    boolean deleteProduct(Long id);
}