package com.example.inventory.event;
public record ShipFailedEvent(String orderId, String reason) {}
