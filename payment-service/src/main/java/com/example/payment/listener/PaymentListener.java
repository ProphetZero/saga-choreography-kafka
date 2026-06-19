package com.example.payment.listener;

import com.example.events.InventoryReservedEvent;
import com.example.events.ShipFailedEvent;
import com.example.payment.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final PaymentService paymentService;

    public PaymentListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Step 2: inventory reserved — charge the customer */
    @KafkaListener(topics = "inventory-reserved", groupId = "payment-service-group")
    public void onInventoryReserved(InventoryReservedEvent event) {
        paymentService.charge(event.orderId());
    }

    /** Compensating: shipping failed — refund the charge */
    @KafkaListener(topics = "ship-failed", groupId = "payment-service-group")
    public void onShipFailed(ShipFailedEvent event) {
        paymentService.refund(event.orderId());
    }
}
