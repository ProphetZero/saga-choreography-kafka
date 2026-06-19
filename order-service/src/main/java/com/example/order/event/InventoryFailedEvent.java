package com.example.order.event;
public record InventoryFailedEvent(String orderId, String reason) {}
