package com.inno.servlettask.service;

import com.inno.servlettask.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 */
public interface UserService {

    /**
     * Аутентификация пользователя
     * @param username имя пользователя
     * @param password пароль
     * @return Optional с пользователем, если аутентификация успешна
     */
    Optional<User> authenticate(String username, String password);

    /**
     * Регистрация нового пользователя
     * @param user данные пользователя
     * @param rawPassword пароль в открытом виде
     * @return зарегистрированный пользователь с присвоенным ID
     * @throws IllegalArgumentException если username или email уже существуют
     */
    User register(User user, String rawPassword);

    /**
     * Поиск пользователя по ID
     * @param id идентификатор пользователя
     * @return Optional с пользователем
     */
    Optional<User> findById(Long id);

    /**
     * Поиск пользователя по имени
     * @param username имя пользователя
     * @return Optional с пользователем
     */
    Optional<User> findByUsername(String username);

    /**
     * Поиск пользователя по email
     * @param email email пользователя
     * @return Optional с пользователем
     */
    Optional<User> findByEmail(String email);

    /**
     * Получение всех пользователей
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Обновление профиля пользователя
     * @param user обновленные данные пользователя
     * @return обновленный пользователь
     */
    User updateProfile(User user);

    /**
     * Смена пароля пользователя
     * @param userId идентификатор пользователя
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return true если пароль успешно изменен
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Проверка доступности username
     * @param username имя пользователя для проверки
     * @return true если username свободен
     */
    boolean isUsernameAvailable(String username);

    /**
     * Проверка доступности email
     * @param email email для проверки
     * @return true если email свободен
     */
    boolean isEmailAvailable(String email);

    /**
     * Удаление пользователя
     * @param id идентификатор пользователя
     * @return true если удаление успешно
     */
    boolean deleteUser(Long id);
}