package com.example.order.event;
public record ShipFailedEvent(String orderId, String reason) {}
