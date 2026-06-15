package com.inno.servlettask.service.impl;

import com.inno.servlettask.dao.ProductDao;
import com.inno.servlettask.dao.impl.ProductDaoImpl;
import com.inno.servlettask.entity.Product;
import com.inno.servlettask.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LogManager.getLogger();
    private final ProductDao productDao;

    public ProductServiceImpl() {
        this.productDao = new ProductDaoImpl();
    }

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Optional<Product> findById(Long id) {
        logger.debug("Finding product by id: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Invalid product id: {}", id);
            return Optional.empty();
        }

        return productDao.findById(id);
    }

    @Override
    public List<Product> findAll() {
        logger.debug("Finding all products");
        return productDao.findAll();
    }

    @Override
    public List<Product> findByPriceLessThan(Integer maxPrice) {
        logger.debug("Finding products with price less than: {}", maxPrice);

        if (maxPrice == null || maxPrice < 0) {
            logger.warn("Invalid max price: {}", maxPrice);
            return List.of();
        }

        return productDao.findByPriceLessThanEqual(maxPrice);
    }

    @Override
    public List<Product> findByPriceGreaterThan(Integer minPrice) {
        logger.debug("Finding products with price greater than: {}", minPrice);

        if (minPrice == null || minPrice < 0) {
            logger.warn("Invalid min price: {}", minPrice);
            return List.of();
        }

        return productDao.findByPriceGreaterThanEqual(minPrice);
    }

    @Override
    public Product createProduct(Product product) {
        logger.info("Creating new product: {}", product.getName());

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        return productDao.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        logger.info("Updating product: {}", product.getName());

        if (product.getId() == null || product.getId() <= 0) {
            throw new IllegalArgumentException("Invalid product id");
        }

        Optional<Product> existingProduct = productDao.findById(product.getId());
        if (existingProduct.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + product.getId());
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        return productDao.update(product);
    }

    @Override
    public boolean deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Invalid product id: {}", id);
            return false;
        }

        return productDao.delete(id);
    }
}