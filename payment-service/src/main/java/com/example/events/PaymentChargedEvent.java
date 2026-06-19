package com.example.events;

public record PaymentChargedEvent(String orderId, String chargeId) {}
