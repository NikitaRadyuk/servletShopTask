package com.inno.servlettask.service.impl;

import com.inno.servlettask.dao.OrderDao;
import com.inno.servlettask.dao.ProductDao;
import com.inno.servlettask.dao.impl.OrderDaoImpl;
import com.inno.servlettask.dao.impl.ProductDaoImpl;
import com.inno.servlettask.entity.Order;
import com.inno.servlettask.entity.OrderItem;
import com.inno.servlettask.entity.Product;
import com.inno.servlettask.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LogManager.getLogger();
    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final ReentrantLock lock = new ReentrantLock();

    public OrderServiceImpl() {
        this.orderDao = new OrderDaoImpl();
        this.productDao = new ProductDaoImpl();
    }

    public OrderServiceImpl(OrderDao orderDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    @Override
    public Optional<Order> findById(Long id) {
        logger.debug("Finding order by id: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Invalid order id: {}", id);
            return Optional.empty();
        }

        return orderDao.findById(id);
    }

    @Override
    public List<Order> findAll() {
        logger.debug("Finding all orders");
        return orderDao.findAll();
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        logger.debug("Finding orders by user id: {}", userId);

        if (userId == null || userId <= 0) {
            logger.warn("Invalid user id: {}", userId);
            return List.of();
        }

        return orderDao.findByUserId(userId);
    }

    @Override
    public Order createOrder(Long userId, Map<Long, Integer> items) {
        logger.info("Creating order for user: {}", userId);

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        lock.lock();
        try {
            List<OrderItem> orderItems = new ArrayList<>();
            Integer totalAmount = 0;

            for (Map.Entry<Long, Integer> entry : items.entrySet()) {
                Long productId = entry.getKey();

                Optional<Product> productOpt = productDao.findById(productId);
                if (productOpt.isEmpty()) {
                    throw new IllegalArgumentException("Product not found with id: " + productId);
                }

                Product product = productOpt.get();

                OrderItem item = new OrderItem();
                item.setProductId(productId);
                item.setProductName(product.getName());
                item.setPriceAtOrder(product.getPrice());
                orderItems.add(item);

                totalAmount = totalAmount + product.getPrice();
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setItems(orderItems);

            Order savedOrder = orderDao.saveWithItems(order);

            logger.info("Order created successfully with id: {}", savedOrder.getId());
            return savedOrder;
        } finally {
            lock.unlock();
        }
    }
}