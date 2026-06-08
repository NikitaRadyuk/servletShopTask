package com.inno.servlettask.controller;

import com.inno.servlettask.entity.Order;
import com.inno.servlettask.service.OrderService;
import com.inno.servlettask.service.ProductService;
import com.inno.servlettask.service.impl.OrderServiceImpl;
import com.inno.servlettask.service.impl.ProductServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/orders/*")
public class OrderController extends HttpServlet {

    private OrderService orderService;
    private ProductService productService;

    @Override
    public void init() {
        orderService = new OrderServiceImpl();
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            path = "/list";
        }

        switch (path) {
            case "/list":
                listOrders(request, response);
                break;
            case "/view":
                viewOrder(request, response);
                break;
            case "/checkout":
                showCheckoutPage(request, response);
                break;
            case "/cancel":
                cancelOrder(request, response);
                break;
            default:
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

        if ("/checkout".equals(path)) {
            createOrder(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long userId = getCurrentUserId(request);
        List<Order> orders = orderService.findByUserId(userId);

        request.setAttribute("orders", orders);

        String success = getFlashMessage(request, "success");
        String error = getFlashMessage(request, "error");
        if (success != null) request.setAttribute("success", success);
        if (error != null) request.setAttribute("error", error);

        request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
    }

    private void viewOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/orders/list");
            return;
        }

        try {
            Long orderId = Long.parseLong(idParam);
            var orderOpt = orderService.findById(orderId);

            if (orderOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Order order = orderOpt.get();
            Long userId = getCurrentUserId(request);

            if (!order.getUserId().equals(userId) && !isAdmin(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders/list");
        }
    }

    @SuppressWarnings("unchecked")
    private void showCheckoutPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Long, ProductController.CartItem> cart =
                (Map<Long, ProductController.CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            setFlashMessage(request, "error", "Your cart is empty");
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        int total = 0;
        for (ProductController.CartItem item : cart.values()) {
            total += item.getSubtotal();
        }

        request.setAttribute("cart", cart.values());
        request.setAttribute("total", total);

        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    private void createOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        Map<Long, ProductController.CartItem> cart =
                (Map<Long, ProductController.CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            setFlashMessage(request, "error", "Your cart is empty");
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        try {
            Map<Long, Integer> items = new HashMap<>();
            for (ProductController.CartItem item : cart.values()) {
                // Проверка наличия товара
                var productOpt = productService.findById(item.getProductId());
                if (productOpt.isEmpty()) {
                    setFlashMessage(request, "error", "Product not found: " + item.getProductName());
                    response.sendRedirect(request.getContextPath() + "/products/cart/view");
                    return;
                }
                items.put(item.getProductId(), item.getQuantity());
            }

            Order order = orderService.createOrder(getCurrentUserId(request), items);

            session.removeAttribute("cart");

            setFlashMessage(request, "success", "Order #" + order.getId() + " created successfully!");
            response.sendRedirect(request.getContextPath() + "/orders/view?id=" + order.getId());

        } catch (IllegalArgumentException e) {
            setFlashMessage(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders/checkout");
        } catch (Exception e) {
            setFlashMessage(request, "error", "Failed to create order: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders/checkout");
        }
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/orders/list");
            return;
        }

        try {
            Long orderId = Long.parseLong(idParam);
            var orderOpt = orderService.findById(orderId);

            if (orderOpt.isEmpty()) {
                setFlashMessage(request, "error", "Order not found");
                response.sendRedirect(request.getContextPath() + "/orders/list");
                return;
            }

            Order order = orderOpt.get();
            Long userId = getCurrentUserId(request);

            if (!order.getUserId().equals(userId) && !isAdmin(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            setFlashMessage(request, "error", "Order cancellation not implemented yet");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders/list");
        }
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

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String role = (String) session.getAttribute("role");
            return "ADMIN".equals(role);
        }
        return false;
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