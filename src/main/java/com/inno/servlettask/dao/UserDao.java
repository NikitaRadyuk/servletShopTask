package com.inno.servlettask.dao;

import com.inno.servlettask.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);

    User update(User user);

    boolean delete(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}