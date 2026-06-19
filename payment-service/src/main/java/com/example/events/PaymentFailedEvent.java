package com.example.events;

public record PaymentFailedEvent(String orderId, String reason) {}
