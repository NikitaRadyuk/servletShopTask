package com.inno.servlettask.validator;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

/**
 * Валидатор для пользователей
 */
public class UserValidator {
    private static final Logger logger = LogManager.getLogger(UserValidator.class);

    // Регулярные выражения
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я\\s-]{2,50}$");

    /**
     * Валидация регистрации пользователя
     */
    public static ValidationResult validateRegistration(String username, String email,
                                                        String password, String confirmPassword) {
        ValidationResult result = new ValidationResult();

        // Валидация username
        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            result.addError("Username must be 3-20 characters long and can only contain letters, numbers, and underscore");
        }

        // Валидация email
        if (email == null || email.trim().isEmpty()) {
            result.addError("Email is required");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("Invalid email format");
        }

        // Валидация пароля
        if (password == null || password.isEmpty()) {
            result.addError("Password is required");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            result.addError("Password must be at least 6 characters long");
        }

        // Проверка совпадения паролей
        if (password != null && confirmPassword != null && !password.equals(confirmPassword)) {
            result.addError("Passwords do not match");
        }

        return result;
    }

    /**
     * Валидация регистрации с HttpServletRequest
     */
    public static boolean validateRegistration(String username, String email,
                                               String password, String confirmPassword,
                                               HttpServletRequest request) {
        ValidationResult result = validateRegistration(username, email, password, confirmPassword);

        if (!result.isValid()) {
            for (String error : result.getErrors()) {
                request.setAttribute("error", error);
                break; // Показываем первую ошибку
            }
            logger.debug("Registration validation failed: {}", result.getErrors());
            return false;
        }

        return true;
    }

    /**
     * Валидация обновления профиля
     */
    public static ValidationResult validateProfileUpdate(String email, String firstName, String lastName) {
        ValidationResult result = new ValidationResult();

        // Валидация email
        if (email == null || email.trim().isEmpty()) {
            result.addError("Email is required");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("Invalid email format");
        }

        // Валидация имени
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(firstName).matches()) {
                result.addError("First name must be 2-50 characters long and can only contain letters");
            }
        }

        // Валидация фамилии
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(lastName).matches()) {
                result.addError("Last name must be 2-50 characters long and can only contain letters");
            }
        }

        return result;
    }

    /**
     * Валидация смены пароля
     */
    public static ValidationResult validatePasswordChange(String currentPassword,
                                                          String newPassword,
                                                          String confirmPassword) {
        ValidationResult result = new ValidationResult();

        // Проверка текущего пароля
        if (currentPassword == null || currentPassword.isEmpty()) {
            result.addError("Current password is required");
        }

        // Проверка нового пароля
        if (newPassword == null || newPassword.isEmpty()) {
            result.addError("New password is required");
        } else if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            result.addError("New password must be at least 6 characters long");
        }

        // Проверка совпадения паролей
        if (newPassword != null && confirmPassword != null && !newPassword.equals(confirmPassword)) {
            result.addError("New passwords do not match");
        }

        // Проверка, что новый пароль отличается от текущего
        if (currentPassword != null && newPassword != null && currentPassword.equals(newPassword)) {
            result.addError("New password must be different from current password");
        }

        return result;
    }

    /**
     * Валидация смены пароля с HttpServletRequest
     */
    public static boolean validatePasswordChange(String currentPassword,
                                                 String newPassword,
                                                 String confirmPassword,
                                                 HttpServletRequest request) {
        ValidationResult result = validatePasswordChange(currentPassword, newPassword, confirmPassword);

        if (!result.isValid()) {
            for (String error : result.getErrors()) {
                request.setAttribute("error", error);
                break;
            }
            logger.debug("Password change validation failed: {}", result.getErrors());
            return false;
        }

        return true;
    }

    /**
     * Валидация email
     */
    public static boolean validateEmail(String email, HttpServletRequest request) {
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("error", "Invalid email format");
            return false;
        }

        return true;
    }

    /**
     * Валидация username
     */
    public static boolean validateUsername(String username, HttpServletRequest request) {
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "Username is required");
            return false;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            request.setAttribute("error",
                    "Username must be 3-20 characters long and can only contain letters, numbers, and underscore");
            return false;
        }

        return true;
    }

    /**
     * Полная валидация пользователя
     */
    public static ValidationResult validateUser(String username, String email,
                                                String firstName, String lastName) {
        ValidationResult result = new ValidationResult();

        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            result.addError("Username must be 3-20 characters long and can only contain letters, numbers, and underscore");
        }

        if (email == null || email.trim().isEmpty()) {
            result.addError("Email is required");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("Invalid email format");
        }

        if (firstName != null && !firstName.trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(firstName).matches()) {
                result.addError("First name must be 2-50 characters long and can only contain letters");
            }
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(lastName).matches()) {
                result.addError("Last name must be 2-50 characters long and can only contain letters");
            }
        }

        return result;
    }
}