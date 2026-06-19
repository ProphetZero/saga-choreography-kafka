package com.example.payment.service;

import com.example.events.PaymentChargedEvent;
import com.example.events.PaymentFailedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PaymentService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // chargeId stored per order so we can refund it if shipping fails
    private final Map<String, String> charges = new ConcurrentHashMap<>();

    // Toggle via POST /payment/fail/true to simulate gateway failures
    private final AtomicBoolean simulateFailure = new AtomicBoolean(false);

    public PaymentService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void charge(String orderId) {
        if (simulateFailure.get()) {
            kafkaTemplate.send("payment-failed", orderId,
                    new PaymentFailedEvent(orderId, "Payment gateway declined (simulation)"));
            System.out.printf("[payment-service] FAILED to charge order %s%n", orderId);
            return;
        }

        var chargeId = "CHG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        charges.put(orderId, chargeId);
        kafkaTemplate.send("payment-charged", orderId, new PaymentChargedEvent(orderId, chargeId));
        System.out.printf("[payment-service] Charged order %s, chargeId=%s%n", orderId, chargeId);
    }

    /** Compensating action: refund when shipping fails. */
    public void refund(String orderId) {
        var chargeId = charges.remove(orderId);
        if (chargeId != null) {
            System.out.printf("[payment-service] Refunded order %s, chargeId=%s%n",
                    orderId, chargeId);
        }
    }

    public void setSimulateFailure(boolean fail) {
        simulateFailure.set(fail);
        System.out.printf("[payment-service] Failure simulation: %s%n", fail);
    }
}
