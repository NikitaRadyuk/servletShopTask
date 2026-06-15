package com.inno.servlettask.service;

import com.inno.servlettask.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> authenticate(String username, String password);

    User register(User user, String rawPassword);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User updateProfile(User user);

    boolean changePassword(Long userId, String oldPassword, String newPassword);

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email);

    boolean deleteUser(Long id);
}