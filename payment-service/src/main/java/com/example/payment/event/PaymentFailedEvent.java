package com.example.payment.event;
public record PaymentFailedEvent(String orderId, String reason) {}
