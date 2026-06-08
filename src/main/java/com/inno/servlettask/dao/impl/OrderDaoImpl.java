package com.inno.servlettask.dao.impl;

import com.inno.servlettask.config.DatabaseConfig;
import com.inno.servlettask.dao.OrderDao;
import com.inno.servlettask.entity.Order;
import com.inno.servlettask.entity.OrderItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDaoImpl implements OrderDao {
    private static final Logger logger = LogManager.getLogger(OrderDaoImpl.class);

    // SQL queries for orders
    private static final String FIND_ORDER_BY_ID =
            "SELECT id, user_id, total_amount FROM orders WHERE id = ?";

    private static final String FIND_ALL_ORDERS =
            "SELECT id, user_id, total_amount FROM orders DESC";

    private static final String FIND_BY_USER_ID =
            "SELECT id, user_id, total_amount FROM orders WHERE user_id = ? DESC";

    private static final String INSERT_ORDER =
            "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";

    private static final String UPDATE_ORDER =
            "UPDATE orders SET total_amount = ? WHERE id = ?";

    private static final String DELETE_ORDER =
            "DELETE FROM orders WHERE id = ?";

    private static final String INSERT_ORDER_ITEM =
            "INSERT INTO order_items (order_id, product_id, product_name, price_at_order) VALUES (?, ?, ?, ?)";

    private static final String FIND_ORDER_ITEMS =
            "SELECT id, order_id, product_id, product_name, price_at_order FROM order_items WHERE order_id = ?";

    private static final String DELETE_ORDER_ITEM =
            "DELETE FROM order_items WHERE id = ?";

    private static final String DELETE_ALL_ORDER_ITEMS =
            "DELETE FROM order_items WHERE order_id = ?";

    @Override
    public Optional<Order> findById(Long id) {
        logger.debug("Finding order by id: {}", id);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ORDER_BY_ID)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItems(id));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            logger.error("Error finding order by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        logger.debug("Finding all orders");
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_ORDERS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error finding all orders", e);
        }
        return orders;
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        logger.debug("Finding orders by user id: {}", userId);
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_USER_ID)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by user id: {}", userId, e);
        }
        return orders;
    }

    @Override
    public Order saveWithItems(Order order) {
        logger.info("Saving new order for user: {}", order.getUserId());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Insert order
                try (PreparedStatement stmt = conn.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setLong(1, order.getUserId());
                    stmt.setInt(2, order.getTotalAmount());

                    stmt.executeUpdate();

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            order.setId(generatedKeys.getLong(1));
                        }
                    }
                }

                // Insert order items
                for (OrderItem item : order.getItems()) {
                    item.setOrderId(order.getId());
                    addOrderItem(item);
                }

                conn.commit();
                logger.info("Order saved successfully with id: {}", order.getId());
                return order;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Error saving order with items", e);
                throw new RuntimeException("Failed to save order", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Error saving order", e);
            throw new RuntimeException("Failed to save order", e);
        }
    }

    @Override
    public Order update(Order order) {
        logger.info("Updating order: {}", order.getId());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ORDER)) {

            stmt.setInt(1, order.getTotalAmount());
            stmt.setLong(2, order.getId());

            stmt.executeUpdate();
            logger.info("Order updated successfully: {}", order.getId());
            return order;
        } catch (SQLException e) {
            logger.error("Error updating order: {}", order.getId(), e);
            throw new RuntimeException("Failed to update order", e);
        }
    }

    @Override
    public OrderItem addOrderItem(OrderItem item) {
        logger.debug("Adding item to order: orderId={}, productId={}", item.getOrderId(), item.getProductId());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ORDER_ITEM, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, item.getOrderId());
            stmt.setLong(2, item.getProductId());
            stmt.setString(3, item.getProductName());
            stmt.setInt(4, item.getPriceAtOrder());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                }
            }
            return item;
        } catch (SQLException e) {
            logger.error("Error adding order item", e);
            throw new RuntimeException("Failed to add order item", e);
        }
    }

    @Override
    public List<OrderItem> findOrderItems(Long orderId) {
        logger.debug("Finding order items for order: {}", orderId);
        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ORDER_ITEMS)) {

            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("id"));
                item.setOrderId(rs.getLong("order_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setPriceAtOrder(rs.getInt("price_at_order"));
                items.add(item);
            }
        } catch (SQLException e) {
            logger.error("Error finding order items for order: {}", orderId, e);
        }
        return items;
    }

    @Override
    public boolean removeOrderItem(Long orderItemId) {
        logger.info("Removing order item: {}", orderItemId);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ORDER_ITEM)) {

            stmt.setLong(1, orderItemId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error removing order item: {}", orderItemId, e);
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        logger.info("Deleting order: {}", id);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_ORDER_ITEMS)) {
                    stmt.setLong(1, id);
                    stmt.executeUpdate();
                }

                // Delete order
                try (PreparedStatement stmt = conn.prepareStatement(DELETE_ORDER)) {
                    stmt.setLong(1, id);
                    int affected = stmt.executeUpdate();

                    if (affected > 0) {
                        conn.commit();
                        logger.info("Order deleted successfully: {}", id);
                        return true;
                    }
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Error deleting order: {}", id, e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Error deleting order: {}", id, e);
            return false;
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setTotalAmount(rs.getInt("total_amount"));
        return order;
    }
}