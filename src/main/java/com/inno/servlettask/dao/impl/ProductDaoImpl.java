package com.inno.servlettask.dao.impl;

import com.inno.servlettask.config.DatabaseConfig;
import com.inno.servlettask.dao.ProductDao;
import com.inno.servlettask.entity.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDaoImpl implements ProductDao {
    private static final Logger logger = LogManager.getLogger();

    // SQL queries
    private static final String FIND_BY_ID =
            "SELECT id, name, description, price FROM products WHERE id = ?";

    private static final String FIND_ALL =
            "SELECT id, name, description, price FROM products ORDER BY id";

    private static final String FIND_BY_PRICE_LESS_THAN =
            "SELECT id, name, description, price FROM products WHERE price <= ? ORDER BY price";

    private static final String FIND_BY_PRICE_GREATER_THAN =
            "SELECT id, name, description, price FROM products WHERE price >= ? ORDER BY price";

    private static final String INSERT =
            "INSERT INTO products (name, description, price) VALUES (?, ?, ?)";

    private static final String UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ? WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM products WHERE id = ?";

    @Override
    public Optional<Product> findById(Long id) {
        logger.debug("Finding product by id: {}", id);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding product by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        logger.debug("Finding all products");
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all products", e);
        }
        return products;
    }

    @Override
    public List<Product> findByPriceLessThanEqual(Integer maxPrice) {
        logger.debug("Finding products with price <= {}", maxPrice);
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_PRICE_LESS_THAN)) {

            stmt.setInt(1, maxPrice);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding products by price <= {}", maxPrice, e);
        }
        return products;
    }

    @Override
    public List<Product> findByPriceGreaterThanEqual(Integer minPrice) {
        logger.debug("Finding products with price >= {}", minPrice);
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_PRICE_GREATER_THAN)) {

            stmt.setInt(1, minPrice);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding products by price >= {}", minPrice, e);
        }
        return products;
    }

    @Override
    public Product save(Product product) {
        logger.info("Saving new product: {}", product.getName());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setInt(3, product.getPrice());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getLong(1));
                    }
                }
            }
            logger.info("Product saved successfully with id: {}", product.getId());
            return product;
        } catch (SQLException e) {
            logger.error("Error saving product: {}", product.getName(), e);
            throw new RuntimeException("Failed to save product", e);
        }
    }

    @Override
    public Product update(Product product) {
        logger.info("Updating product: {}", product.getName());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setInt(3, product.getPrice());
            stmt.setLong(4, product.getId());

            stmt.executeUpdate();
            logger.info("Product updated successfully: {}", product.getName());
            return product;
        } catch (SQLException e) {
            logger.error("Error updating product: {}", product.getName(), e);
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        logger.info("Deleting product with id: {}", id);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setLong(1, id);
            int affected = stmt.executeUpdate();

            if (affected > 0) {
                logger.info("Product deleted successfully: {}", id);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error deleting product with id: {}", id, e);
            return false;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getInt("price"));
        return product;
    }
}