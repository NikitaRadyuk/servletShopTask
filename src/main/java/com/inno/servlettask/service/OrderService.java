package com.inno.servlettask.service;

import com.inno.servlettask.entity.Order;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {

    Optional<Order> findById(Long id);

    List<Order> findAll();

    List<Order> findByUserId(Long userId);

    Order createOrder(Long userId, Map<Long, Integer> items);
}