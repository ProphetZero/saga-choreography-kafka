package com.example.events;

public record OrderShippedEvent(String orderId, String trackingNumber) {}
