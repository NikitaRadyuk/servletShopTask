package com.inno.servlettask.dao;

import com.inno.servlettask.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    /**
     * Находит пользователя по ID
     */
    Optional<User> findById(Long id);

    /**
     * Находит пользователя по имени пользователя
     */
    Optional<User> findByUsername(String username);

    /**
     * Находит пользователя по email
     */
    Optional<User> findByEmail(String email);

    /**
     * Возвращает всех пользователей
     */
    List<User> findAll();

    /**
     * Сохраняет нового пользователя
     */
    User save(User user);

    /**
     * Обновляет существующего пользователя
     */
    User update(User user);

    /**
     * Удаляет пользователя по ID
     */
    boolean delete(Long id);

    /**
     * Проверяет существование пользователя по username
     */
    boolean existsByUsername(String username);

    /**
     * Проверяет существование пользователя по email
     */
    boolean existsByEmail(String email);
}