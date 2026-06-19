package com.example.inventory.event;
public record InventoryReservedEvent(String orderId, String item, int quantity) {}
