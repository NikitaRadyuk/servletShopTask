package com.inno.servlettask.dao;

import com.inno.servlettask.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    /**
     * Находит товар по ID
     */
    Optional<Product> findById(Long id);

    /**
     * Возвращает все товары
     */
    List<Product> findAll();

    /**
     * Находит товары по цене (меньше или равно)
     */
    List<Product> findByPriceLessThanEqual(Integer maxPrice);

    /**
     * Находит товары по цене (больше или равно)
     */
    List<Product> findByPriceGreaterThanEqual(Integer minPrice);

    /**
     * Сохраняет новый товар
     */
    Product save(Product product);

    /**
     * Обновляет существующий товар
     */
    Product update(Product product);

    /**
     * Удаляет товар по ID
     */
    boolean delete(Long id);
}