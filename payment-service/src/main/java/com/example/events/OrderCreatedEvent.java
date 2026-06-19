package com.example.events;

public record OrderCreatedEvent(String orderId, String item, int quantity) {}
