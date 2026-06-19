package com.example.inventory.service;

import com.example.events.InventoryFailedEvent;
import com.example.events.InventoryReservedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Simulated stock levels. "tablet" is out of stock to demonstrate failure.
    private final Map<String, Integer> stock = new ConcurrentHashMap<>(Map.of(
        "laptop", 10,
        "phone",  5,
        "tablet", 0
    ));

    // Tracks what was reserved per order so we can release it on compensation.
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    public InventoryService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void reserve(String orderId, String item, int quantity) {
        int available = stock.getOrDefault(item, 0);

        if (available >= quantity) {
            stock.put(item, available - quantity);
            reservations.put(orderId, new Reservation(item, quantity));
            kafkaTemplate.send("inventory-reserved", orderId,
                    new InventoryReservedEvent(orderId, item, quantity));
            System.out.printf("[inventory-service] Reserved %d x %s for order %s%n",
                    quantity, item, orderId);
        } else {
            kafkaTemplate.send("inventory-failed", orderId,
                    new InventoryFailedEvent(orderId,
                            "Insufficient stock for " + item +
                            " (available=" + available + ", requested=" + quantity + ")"));
            System.out.printf("[inventory-service] FAILED to reserve %s for order %s%n",
                    item, orderId);
        }
    }

    /** Compensating action: put stock back when payment or shipping fails. */
    public void release(String orderId) {
        var res = reservations.remove(orderId);
        if (res != null) {
            stock.merge(res.item(), res.quantity(), Integer::sum);
            System.out.printf("[inventory-service] Released %d x %s for order %s%n",
                    res.quantity(), res.item(), orderId);
        }
    }

    private record Reservation(String item, int quantity) {}
}
