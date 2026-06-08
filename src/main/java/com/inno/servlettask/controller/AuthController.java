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

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        if (path == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (path) {
            case "/login":
                showLoginPage(request, response);
                break;
            case "/register":
                showRegisterPage(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        if (path == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (path) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        String error = getFlashMessage(request, "error");
        if (error != null) {
            request.setAttribute("error", error);
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        String error = getFlashMessage(request, "error");
        if (error != null) {
            request.setAttribute("error", error);
        }

        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            setFlashMessage(request, "error", "Username and password are required");
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        var userOpt = userService.authenticate(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setMaxInactiveInterval(1800);

            setFlashMessage(request, "success", "Welcome back, " + user.getFirstName() + "!");
            response.sendRedirect(request.getContextPath() + "/products");
        } else {
            setFlashMessage(request, "error", "Invalid username or password");
            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        // Валидация
        if (!password.equals(confirmPassword)) {
            setFlashMessage(request, "error", "Passwords do not match");
            response.sendRedirect(request.getContextPath() + "/auth/register");
            return;
        }

        if (password == null || password.length() < 6) {
            setFlashMessage(request, "error", "Password must be at least 6 characters");
            response.sendRedirect(request.getContextPath() + "/auth/register");
            return;
        }

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);

            User registeredUser = userService.register(newUser, password);

            setFlashMessage(request, "success", "Registration successful! Please login.");
            response.sendRedirect(request.getContextPath() + "/auth/login");

        } catch (IllegalArgumentException e) {
            setFlashMessage(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/auth/register");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/auth/login");
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