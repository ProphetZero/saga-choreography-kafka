package com.example.events;

public record InventoryFailedEvent(String orderId, String reason) {}
