package com.inno.servlettask.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(AuthFilter.class);
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login", "/register", "/products", "/", "/index.jsp"
    );
    private static final Set<String> STATIC_RESOURCES = Set.of(
            "/css/", "/js/", "/images/"
    );
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (isPublicPath(path) || isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            logger.warn("Unauthorized access attempt to: {}", path);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Проверяем роль для админских путей
        if (path.startsWith("/admin") && !"ADMIN".equals(session.getAttribute("role"))) {
            logger.warn("Access denied for non-admin user to: {}", path);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isStaticResource(String path) {
        return STATIC_RESOURCES.stream().anyMatch(path::startsWith);
    }
}