package com.example.events;

public record OrderCancelledEvent(String orderId, String reason) {}
