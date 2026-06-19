package com.example.events;

public record InventoryReservedEvent(String orderId, String item, int quantity) {}
