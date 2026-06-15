package com.inno.servlettask.dao;

import com.inno.servlettask.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findByPriceLessThanEqual(Integer maxPrice);

    List<Product> findByPriceGreaterThanEqual(Integer minPrice);

    Product save(Product product);

    Product update(Product product);

    boolean delete(Long id);
}