package com.inno.servlettask.service;

import com.inno.servlettask.entity.Order;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для работы с заказами
 */
public interface OrderService {

    /**
     * Поиск заказа по ID
     * @param id идентификатор заказа
     * @return Optional с заказом
     */
    Optional<Order> findById(Long id);

    /**
     * Получение всех заказов
     * @return список всех заказов
     */
    List<Order> findAll();

    /**
     * Получение заказов пользователя
     * @param userId идентификатор пользователя
     * @return список заказов пользователя
     */
    List<Order> findByUserId(Long userId);

    /**
     * Создание нового заказа
     * @param userId идентификатор пользователя
     * @param items карта товаров (productId -> количество)
     * @return созданный заказ
     */
    Order createOrder(Long userId, Map<Long, Integer> items);
}