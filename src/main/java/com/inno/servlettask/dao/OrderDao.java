package com.inno.servlettask.dao;

import com.inno.servlettask.entity.Order;
import com.inno.servlettask.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> findById(Long id);

    List<Order> findAll();

    List<Order> findByUserId(Long userId);

    Order saveWithItems(Order order);

    Order update(Order order);

    OrderItem addOrderItem(OrderItem item);

    List<OrderItem> findOrderItems(Long orderId);

    boolean removeOrderItem(Long orderItemId);

    boolean delete(Long id);
}