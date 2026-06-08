<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Корзина - Интернет-магазин</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .cart-container {
            max-width: 1000px;
            margin: 40px auto;
        }
        h1 {
            margin-bottom: 30px;
        }
        table {
            width: 100%;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            border-collapse: collapse;
        }
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background: #f8f9fa;
        }
        .quantity-input {
            width: 60px;
            padding: 5px;
            text-align: center;
        }
        .cart-summary {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-top: 30px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        .total-row {
            font-size: 20px;
            font-weight: bold;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }
        .btn-primary {
            background: #3498db;
            color: white;
        }
        .btn-danger {
            background: #e74c3c;
            color: white;
        }
        .btn-secondary {
            background: #95a5a6;
            color: white;
        }
        .empty-cart {
            text-align: center;
            padding: 50px;
            background: white;
            border-radius: 10px;
        }
        .alert {
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .alert-success {
            background: #d4edda;
            color: #155724;
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="container cart-container">
    <h1>Корзина</h1>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty cart}">
            <div class="empty-cart">
                <p style="font-size: 48px; margin-bottom: 20px;">🛒</p>
                <p style="font-size: 18px; margin-bottom: 20px;">Ваша корзина пуста</p>
                <a href="${pageContext.request.contextPath}/products/list" class="btn btn-primary">Перейти к покупкам</a>
            </div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Товар</th>
                    <th>Цена</th>
                    <th>Количество</th>
                    <th>Сумма</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${cart}" var="item">
                    <tr>
                        <td>${item.productName}</td>
                        <td>${item.price} ₽</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/products/cart/update" method="get" style="display: flex; gap: 5px;">
                                <input type="hidden" name="id" value="${item.productId}">
                                <input type="number" name="quantity" value="${item.quantity}" min="1" class="quantity-input">
                                <button type="submit" class="btn btn-secondary" style="padding: 5px 10px;">Обновить</button>
                            </form>
                        </td>
                        <td>${item.price * item.quantity} ₽</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/products/cart/remove?id=${item.productId}" class="btn btn-danger" style="padding: 5px 10px;">Удалить</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="cart-summary">
                <div class="total-row">
                    <strong>Итого: ${total} ₽</strong>
                </div>
                <div style="display: flex; gap: 15px; margin-top: 20px;">
                    <a href="${pageContext.request.contextPath}/products/list" class="btn btn-secondary">Продолжить покупки</a>
                    <a href="${pageContext.request.contextPath}/orders/checkout" class="btn btn-primary">Оформить заказ</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>