package com.inno.servlettask.service.impl;

import com.inno.servlettask.dao.UserDao;
import com.inno.servlettask.dao.impl.UserDaoImpl;
import com.inno.servlettask.entity.User;
import com.inno.servlettask.service.UserService;
import com.inno.servlettask.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger();
    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        logger.debug("Authenticating user: {}", username);

        if (username == null || username.trim().isBlank()) {
            logger.warn("Authentication failed: empty username");
            return Optional.empty();
        }

        if (password == null || password.isBlank()) {
            logger.warn("Authentication failed: empty password for user {}", username);
            return Optional.empty();
        }

        Optional<User> userOpt = userDao.findByUsername(username);

        if (userOpt.isPresent() && PasswordUtil.verifyPassword(password, userOpt.get().getPasswordHash())) {
            logger.info("User authenticated successfully: {}", username);
            return userOpt;
        }

        logger.warn("Authentication failed for user: {}", username);
        return Optional.empty();
    }

    @Override
    public User register(User user, String rawPassword) {
        logger.info("Registering new user: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (!isUsernameAvailable(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        if (!isEmailAvailable(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        String hashedPassword = PasswordUtil.hashPassword(rawPassword);
        user.setPasswordHash(hashedPassword);

        if (user.getRole() == null) {
            user.setRole("USER");
        }

        User savedUser = userDao.save(user);
        logger.info("User registered successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by id: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Invalid user id: {}", id);
            return Optional.empty();
        }

        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Empty username provided");
            return Optional.empty();
        }

        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Empty email provided");
            return Optional.empty();
        }

        return userDao.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        logger.debug("Finding all users");
        return userDao.findAll();
    }

    @Override
    public User updateProfile(User user) {
        logger.info("Updating profile for user: {}", user.getUsername());

        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }

        Optional<User> existingUser = userDao.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }

        if (!existingUser.get().getUsername().equals(user.getUsername()) &&
                !isUsernameAvailable(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + user.getUsername());
        }

        if (!existingUser.get().getEmail().equals(user.getEmail()) &&
                !isEmailAvailable(user.getEmail())) {
            throw new IllegalArgumentException("Email already used: " + user.getEmail());
        }

        user.setPasswordHash(existingUser.get().getPasswordHash());

        return userDao.update(user);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Changing password for user id: {}", userId);

        if (userId == null || userId <= 0) {
            logger.warn("Invalid user id: {}", userId);
            return false;
        }

        if (newPassword == null || newPassword.length() < 6) {
            logger.warn("New password is too short");
            return false;
        }

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with id: {}", userId);
            return false;
        }

        User user = userOpt.get();

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            logger.warn("Old password is incorrect for user: {}", user.getUsername());
            return false;
        }

        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        user.setPasswordHash(newHashedPassword);
        userDao.update(user);

        logger.info("Password changed successfully for user: {}", user.getUsername());
        return true;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        logger.debug("Checking username availability: {}", username);

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        return !userDao.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        logger.debug("Checking email availability: {}", email);

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return !userDao.existsByEmail(email);
    }

    @Override
    public boolean deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Invalid user id: {}", id);
            return false;
        }

        return userDao.delete(id);
    }
}