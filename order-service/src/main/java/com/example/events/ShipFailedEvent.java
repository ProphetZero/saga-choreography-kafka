package com.example.events;

public record ShipFailedEvent(String orderId, String reason) {}
