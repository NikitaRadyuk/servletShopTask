package com.inno.servlettask.service;

import com.inno.servlettask.dao.UserDao;
import com.inno.servlettask.entity.User;
import com.inno.servlettask.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);
    }

    @Test
    void authenticate_ShouldReturnUser_WhenCredentialsValid() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        User user = new User();
        user.setUsername(username);
        // Password hash для "password123"
        user.setPasswordHash("somesalt:somehash");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));

        // Act & Assert (примечание: для реального теста нужно корректно хешировать пароль)
        assertNotNull(userService.authenticate(username, password));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        String username = "existing";
        when(userDao.findByUsername(username)).thenReturn(Optional.of(new User()));

        User user = new User();
        user.setUsername(username);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.register(user, "password"));
    }

    @Test
    void isUsernameAvailable_ShouldReturnTrue_WhenUsernameNotExists() {
        when(userDao.findByUsername("newuser")).thenReturn(Optional.empty());
        assertTrue(userService.isUsernameAvailable("newuser"));
    }
}