package com.example.inventory.listener;

import com.example.events.OrderCreatedEvent;
import com.example.events.PaymentFailedEvent;
import com.example.events.ShipFailedEvent;
import com.example.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    private final InventoryService inventoryService;

    public InventoryListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** Step 1: order created — try to reserve stock */
    @KafkaListener(topics = "order-created", groupId = "inventory-service-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserve(event.orderId(), event.item(), event.quantity());
    }

    /** Compensating: payment failed — release the reserved stock */
    @KafkaListener(topics = "payment-failed", groupId = "inventory-service-group")
    public void onPaymentFailed(PaymentFailedEvent event) {
        inventoryService.release(event.orderId());
    }

    /** Compensating: shipping failed — release the reserved stock */
    @KafkaListener(topics = "ship-failed", groupId = "inventory-service-group")
    public void onShipFailed(ShipFailedEvent event) {
        inventoryService.release(event.orderId());
    }
}
