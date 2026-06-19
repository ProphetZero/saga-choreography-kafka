package com.example.order.event;
public record PaymentFailedEvent(String orderId, String reason) {}
