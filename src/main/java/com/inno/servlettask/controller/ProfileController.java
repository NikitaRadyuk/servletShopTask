package com.inno.servlettask.controller;

import com.inno.servlettask.entity.User;
import com.inno.servlettask.service.UserService;
import com.inno.servlettask.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile/*")
public class ProfileController extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String path = request.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/view")) {
            viewProfile(request, response);
        } else if ("/edit".equals(path)) {
            showEditForm(request, response);
        } else if ("/change-password".equals(path)) {
            showChangePasswordForm(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String path = request.getPathInfo();

        if ("/edit".equals(path)) {
            updateProfile(request, response);
        } else if ("/change-password".equals(path)) {
            changePassword(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void viewProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getCurrentUser(request);
        request.setAttribute("user", user);

        String success = getFlashMessage(request, "success");
        String error = getFlashMessage(request, "error");
        if (success != null) request.setAttribute("success", success);
        if (error != null) request.setAttribute("error", error);

        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getCurrentUser(request);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/profile-edit.jsp").forward(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            setFlashMessage(request, "error", "Email is required");
            response.sendRedirect(request.getContextPath() + "/profile/edit");
            return;
        }

        try {
            User currentUser = getCurrentUser(request);
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);

            userService.updateProfile(currentUser);

            HttpSession session = request.getSession();
            session.setAttribute("user", currentUser);

            setFlashMessage(request, "success", "Profile updated successfully!");
            response.sendRedirect(request.getContextPath() + "/profile/view");

        } catch (IllegalArgumentException e) {
            setFlashMessage(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/profile/edit");
        }
    }

    private void showChangePasswordForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/change-password.jsp").forward(request, response);
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            setFlashMessage(request, "error", "All fields are required");
            response.sendRedirect(request.getContextPath() + "/profile/change-password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            setFlashMessage(request, "error", "New passwords do not match");
            response.sendRedirect(request.getContextPath() + "/profile/change-password");
            return;
        }

        if (newPassword.length() < 6) {
            setFlashMessage(request, "error", "New password must be at least 6 characters");
            response.sendRedirect(request.getContextPath() + "/profile/change-password");
            return;
        }

        try {
            Long userId = getCurrentUserId(request);
            boolean changed = userService.changePassword(userId, currentPassword, newPassword);

            if (changed) {
                setFlashMessage(request, "success", "Password changed successfully!");
                response.sendRedirect(request.getContextPath() + "/profile/view");
            } else {
                setFlashMessage(request, "error", "Current password is incorrect");
                response.sendRedirect(request.getContextPath() + "/profile/change-password");
            }
        } catch (Exception e) {
            setFlashMessage(request, "error", "Failed to change password: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/profile/change-password");
        }
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Long) session.getAttribute("userId");
        }
        return null;
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private void setFlashMessage(HttpServletRequest request, String type, String message) {
        request.getSession().setAttribute(type, message);
    }

    private String getFlashMessage(HttpServletRequest request, String type) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String message = (String) session.getAttribute(type);
            session.removeAttribute(type);
            return message;
        }
        return null;
    }
}