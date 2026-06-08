package com.inno.servlettask.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private Long userId;
    private Integer totalAmount;
    private String address;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Order(Long id, Long userId, Integer totalAmount, String shippingAddress, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.address = shippingAddress;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(userId, order.userId) && Objects.equals(totalAmount, order.totalAmount) && Objects.equals(address, order.address) && Objects.equals(items, order.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, totalAmount, address, items);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", shippingAddress='" + address + '\'' +
                ", items=" + items +
                '}';
    }
}
