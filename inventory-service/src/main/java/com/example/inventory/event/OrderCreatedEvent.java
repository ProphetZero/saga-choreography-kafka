package com.example.inventory.event;
public record OrderCreatedEvent(String orderId, String item, int quantity) {}
