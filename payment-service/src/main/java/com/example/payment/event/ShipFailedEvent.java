package com.example.payment.event;
public record ShipFailedEvent(String orderId, String reason) {}
