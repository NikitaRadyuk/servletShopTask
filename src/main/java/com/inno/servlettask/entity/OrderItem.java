package com.inno.servlettask.entity;

import java.util.Objects;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer priceAtOrder;

    public OrderItem() {}

    public OrderItem(Long productId, String productName, Integer priceAtOrder) {
        this.productId = productId;
        this.productName = productName;
        this.priceAtOrder = priceAtOrder;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(Integer priceAtOrder) { this.priceAtOrder = priceAtOrder; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", priceAtOrder=" + priceAtOrder +
                '}';
    }
}
