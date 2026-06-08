<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Оформление заказа - Интернет-магазин</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .checkout-container {
            max-width: 1000px;
            margin: 40px auto;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
        }
        .order-summary, .shipping-form {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        h2 {
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #eee;
        }
        .cart-item {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .total {
            margin-top: 20px;
            padding-top: 10px;
            font-size: 18px;
            font-weight: bold;
            text-align: right;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
        }
        textarea {
            resize: vertical;
            min-height: 80px;
        }
        .btn {
            width: 100%;
            padding: 12px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
        }
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        @media (max-width: 768px) {
            .checkout-container {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="container checkout-container">
    <div class="order-summary">
        <h2>Ваш заказ</h2>
        <c:forEach items="${cart}" var="item">
            <div class="cart-item">
                <span>${item.productName} x ${item.quantity}</span>
                <span>${item.price * item.quantity} ₽</span>
            </div>
        </c:forEach>
        <div class="total">
            Итого: ${total} ₽
        </div>
    </div>

    <div class="shipping-form">
        <h2>Данные для доставки</h2>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/orders/checkout" method="post">
            <div class="form-group">
                <label for="shippingAddress">Адрес доставки *</label>
                <textarea id="shippingAddress" name="shippingAddress" required
                          placeholder="Улица, дом, квартира, город, индекс"></textarea>
            </div>

            <button type="submit" class="btn">Подтвердить заказ</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>