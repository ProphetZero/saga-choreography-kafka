package com.example.payment.event;
public record PaymentChargedEvent(String orderId, double amount) {}
