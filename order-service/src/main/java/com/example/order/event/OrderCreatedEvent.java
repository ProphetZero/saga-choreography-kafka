package com.example.order.event;
public record OrderCreatedEvent(String orderId, String item, int quantity) {}
