package com.inno.servlettask.service;

import com.inno.servlettask.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с товарами
 */
public interface ProductService {

    /**
     * Поиск товара по ID
     * @param id идентификатор товара
     * @return Optional с товаром
     */
    Optional<Product> findById(Long id);

    /**
     * Получение всех товаров
     * @return список всех товаров
     */
    List<Product> findAll();

    /**
     * Поиск товаров дешевле указанной цены
     * @param maxPrice максимальная цена
     * @return список товаров
     */
    List<Product> findByPriceLessThan(Integer maxPrice);

    /**
     * Поиск товаров дороже указанной цены
     * @param minPrice минимальная цена
     * @return список товаров
     */
    List<Product> findByPriceGreaterThan(Integer minPrice);

    /**
     * Создание нового товара
     * @param product товар для создания
     * @return созданный товар с присвоенным ID
     */
    Product createProduct(Product product);

    /**
     * Обновление товара
     * @param product обновленные данные товара
     * @return обновленный товар
     */
    Product updateProduct(Product product);

    /**
     * Удаление товара
     * @param id идентификатор товара
     * @return true если удаление успешно
     */
    boolean deleteProduct(Long id);
}