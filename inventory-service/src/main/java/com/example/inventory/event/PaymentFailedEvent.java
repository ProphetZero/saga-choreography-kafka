package com.example.inventory.event;
public record PaymentFailedEvent(String orderId, String reason) {}
