package com.example.inventory.event;
public record InventoryFailedEvent(String orderId, String reason) {}
