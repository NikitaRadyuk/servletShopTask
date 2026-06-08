package com.inno.servlettask.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения результатов валидации
 */
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();
    private boolean valid = true;

    public void addError(String error) {
        errors.add(error);
        valid = false;
    }

    public void addErrors(List<String> newErrors) {
        errors.addAll(newErrors);
        if (!newErrors.isEmpty()) {
            valid = false;
        }
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorsAsString() {
        return String.join(", ", errors);
    }

    public String getErrorsAsHtml() {
        StringBuilder sb = new StringBuilder();
        for (String error : errors) {
            sb.append("<li>").append(error).append("</li>");
        }
        return sb.toString();
    }

    public void clear() {
        errors.clear();
        valid = true;
    }

    public static ValidationResult success() {
        return new ValidationResult();
    }

    public static ValidationResult error(String error) {
        ValidationResult result = new ValidationResult();
        result.addError(error);
        return result;
    }
}