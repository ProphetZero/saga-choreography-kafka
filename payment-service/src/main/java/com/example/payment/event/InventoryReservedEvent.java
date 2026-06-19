package com.example.payment.event;
public record InventoryReservedEvent(String orderId, String item, int quantity) {}
