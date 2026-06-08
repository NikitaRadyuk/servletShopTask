package com.inno.servlettask.dao.impl;

import com.inno.servlettask.config.DatabaseConfig;
import com.inno.servlettask.dao.UserDao;
import com.inno.servlettask.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = LogManager.getLogger();

    private static final String FIND_BY_ID =
            "SELECT id, username, email, password_hash, first_name, last_name, role, created_at FROM users WHERE id = ?";

    private static final String FIND_BY_USERNAME =
            "SELECT id, username, email, password_hash, first_name, last_name, role, created_at FROM users WHERE username = ?";

    private static final String FIND_BY_EMAIL =
            "SELECT id, username, email, password_hash, first_name, last_name, role, created_at FROM users WHERE email = ?";

    private static final String FIND_ALL =
            "SELECT id, username, email, password_hash, first_name, last_name, role, created_at FROM users ORDER BY id";

    private static final String INSERT =
            "INSERT INTO users (username, email, password_hash, first_name, last_name, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE users SET username = ?, email = ?, first_name = ?, last_name = ? WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM users WHERE id = ?";

    private static final String EXISTS_BY_USERNAME =
            "SELECT COUNT(*) FROM users WHERE username = ?";

    private static final String EXISTS_BY_EMAIL =
            "SELECT COUNT(*) FROM users WHERE email = ?";

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by id: {}", id);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_USERNAME)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMAIL)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        logger.debug("Finding all users");
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }
        return users;
    }

    @Override
    public User save(User user) {
        logger.info("Saving new user: {}", user.getUsername());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getRole());
            stmt.setTimestamp(7, Timestamp.valueOf(user.getCreatedAt()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
            logger.info("User saved successfully with id: {}", user.getId());
            return user;
        } catch (SQLException e) {
            logger.error("Error saving user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public User update(User user) {
        logger.info("Updating user: {}", user.getUsername());

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setLong(5, user.getId());

            stmt.executeUpdate();
            logger.info("User updated successfully: {}", user.getUsername());
            return user;
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        logger.info("Deleting user with id: {}", id);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setLong(1, id);
            int affected = stmt.executeUpdate();

            if (affected > 0) {
                logger.info("User deleted successfully: {}", id);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error deleting user with id: {}", id, e);
            return false;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        logger.debug("Checking existence of username: {}", username);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_USERNAME)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking username existence: {}", username, e);
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Checking existence of email: {}", email);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_EMAIL)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking email existence: {}", email, e);
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}