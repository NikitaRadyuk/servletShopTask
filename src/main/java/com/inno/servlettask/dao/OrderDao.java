package com.inno.servlettask.dao;

import com.inno.servlettask.entity.Order;
import com.inno.servlettask.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    /**
     * Находит заказ по ID
     */
    Optional<Order> findById(Long id);

    /**
     * Возвращает все заказы
     */
    List<Order> findAll();

    /**
     * Находит заказы пользователя по ID пользователя
     */
    List<Order> findByUserId(Long userId);

    /**
     * Сохраняет новый заказ вместе с его позициями (транзакционно)
     */
    Order saveWithItems(Order order);

    /**
     * Обновляет существующий заказ
     */
    Order update(Order order);

    /**
     * Добавляет позицию в заказ
     */
    OrderItem addOrderItem(OrderItem item);

    /**
     * Находит все позиции заказа
     */
    List<OrderItem> findOrderItems(Long orderId);

    /**
     * Удаляет позицию из заказа
     */
    boolean removeOrderItem(Long orderItemId);

    /**
     * Удаляет заказ по ID (только если статус CANCELLED или PENDING)
     */
    boolean delete(Long id);
}