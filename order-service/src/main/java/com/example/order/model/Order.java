package com.example.order.model;

public class Order {
    private final String id;
    private final String item;
    private final int quantity;
    private OrderStatus status;

    public Order(String id, String item, int quantity, OrderStatus status) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }

    public String getId()                     { return id; }
    public String getItem()                   { return item; }
    public int getQuantity()                  { return quantity; }
    public OrderStatus getStatus()            { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
