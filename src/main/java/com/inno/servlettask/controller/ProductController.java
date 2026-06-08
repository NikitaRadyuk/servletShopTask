package com.inno.servlettask.controller;

import com.inno.servlettask.entity.Product;
import com.inno.servlettask.service.ProductService;
import com.inno.servlettask.service.impl.ProductServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/products/*")
public class ProductController extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            path = "/list";
        }

        switch (path) {
            case "/list":
                listProducts(request, response);
                break;
            case "/view":
                viewProduct(request, response);
                break;
            case "/add":
                if (isAdmin(request)) {
                    showAddForm(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/edit":
                if (isAdmin(request)) {
                    showEditForm(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/delete":
                if (isAdmin(request)) {
                    deleteProduct(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/cart/add":
                addToCart(request, response);
                break;
            case "/cart/remove":
                removeFromCart(request, response);
                break;
            case "/cart/view":
                viewCart(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        switch (path) {
            case "/add":
                if (isAdmin(request)) {
                    addProduct(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/edit":
                if (isAdmin(request)) {
                    updateProduct(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Product> products = productService.findAll();
        request.setAttribute("products", products);

        String success = getFlashMessage(request, "success");
        String error = getFlashMessage(request, "error");
        if (success != null) request.setAttribute("success", success);
        if (error != null) request.setAttribute("error", error);

        request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
    }

    private void viewProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            var productOpt = productService.findById(id);

            if (productOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("product", productOpt.get());
            request.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products/list");
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(request, response);
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");;

        try {
            Integer price = Integer.parseInt(priceStr);

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);

            productService.createProduct(product);

            setFlashMessage(request, "success", "Product added successfully!");
            response.sendRedirect(request.getContextPath() + "/products/list");

        } catch (NumberFormatException e) {
            setFlashMessage(request, "error", "Invalid price or stock quantity");
            response.sendRedirect(request.getContextPath() + "/products/add");
        } catch (IllegalArgumentException e) {
            setFlashMessage(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/products/add");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            var productOpt = productService.findById(id);

            if (productOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("product", productOpt.get());
            request.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products/list");
        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Integer price = Integer.parseInt(priceStr);

            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);

            productService.updateProduct(product);

            setFlashMessage(request, "success", "Product updated successfully!");
            response.sendRedirect(request.getContextPath() + "/products/list");

        } catch (NumberFormatException e) {
            setFlashMessage(request, "error", "Invalid price or stock quantity");
            response.sendRedirect(request.getContextPath() + "/products/edit?id=" + idParam);
        } catch (IllegalArgumentException e) {
            setFlashMessage(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/products/edit?id=" + idParam);
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            productService.deleteProduct(id);
            setFlashMessage(request, "success", "Product deleted successfully!");
        } catch (Exception e) {
            setFlashMessage(request, "error", "Failed to delete product");
        }

        response.sendRedirect(request.getContextPath() + "/products/list");
    }

    @SuppressWarnings("unchecked")
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }

        try {
            Long productId = Long.parseLong(idParam);
            var productOpt = productService.findById(productId);

            if (productOpt.isEmpty()) {
                setFlashMessage(request, "error", "Product not found");
                response.sendRedirect(request.getContextPath() + "/products/list");
                return;
            }

            Product product = productOpt.get();

            HttpSession session = request.getSession();
            Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new HashMap<>();
                session.setAttribute("cart", cart);
            }

            if (cart.containsKey(productId)) {
                CartItem item = cart.get(productId);
                item.setQuantity(item.getQuantity() + 1);
            } else {
                CartItem item = new CartItem();
                item.setProductId(productId);
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(1);
                cart.put(productId, item);
            }

            setFlashMessage(request, "success", product.getName() + " added to cart!");
            response.sendRedirect(request.getContextPath() + "/products/list");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products/list");
        }
    }

    @SuppressWarnings("unchecked")
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/products/cart/view");
            return;
        }

        try {
            Long productId = Long.parseLong(idParam);
            HttpSession session = request.getSession();
            Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");

            if (cart != null) {
                cart.remove(productId);
                setFlashMessage(request, "success", "Item removed from cart");
            }

        } catch (NumberFormatException e) {
            // ignore
        }

        response.sendRedirect(request.getContextPath() + "/products/cart/view");
    }

    @SuppressWarnings("unchecked")
    private void viewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        int total = 0;
        for (CartItem item : cart.values()) {
            total += item.getPrice() * item.getQuantity();
        }

        request.setAttribute("cart", new ArrayList<>(cart.values()));
        request.setAttribute("total", total);

        String success = getFlashMessage(request, "success");
        String error = getFlashMessage(request, "error");
        if (success != null) request.setAttribute("success", success);
        if (error != null) request.setAttribute("error", error);

        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
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

    public static class CartItem {
        private Long productId;
        private String productName;
        private Integer price;
        private int quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public Integer getSubtotal() { return price * quantity; }
    }
}