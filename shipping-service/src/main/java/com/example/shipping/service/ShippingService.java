package com.example.shipping.service;

import com.example.events.OrderShippedEvent;
import com.example.events.ShipFailedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ShippingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AtomicBoolean simulateFailure = new AtomicBoolean(false);

    public ShippingService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void ship(String orderId) {
        if (simulateFailure.get()) {
            kafkaTemplate.send("ship-failed", orderId,
                    new ShipFailedEvent(orderId, "No delivery slots available (simulation)"));
            System.out.printf("[shipping-service] FAILED to ship order %s%n", orderId);
            return;
        }

        var tracking = "TRACK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        kafkaTemplate.send("order-shipped", orderId, new OrderShippedEvent(orderId, tracking));
        System.out.printf("[shipping-service] Order %s SHIPPED, tracking=%s%n", orderId, tracking);
    }

    public void setSimulateFailure(boolean fail) {
        simulateFailure.set(fail);
        System.out.printf("[shipping-service] Failure simulation: %s%n", fail);
    }
}
