package com.example.order.listener;

import com.example.events.*;
import com.example.order.model.OrderStatus;
import com.example.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens to every downstream saga event.
 * Either advances order state on success or triggers a compensating cancel on failure.
 */
@Component
public class OrderSagaListener {

    private final OrderService orderService;

    public OrderSagaListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "inventory-reserved", groupId = "order-service-group")
    public void onInventoryReserved(InventoryReservedEvent event) {
        orderService.updateStatus(event.orderId(), OrderStatus.INVENTORY_RESERVED);
    }

    /** Compensating: inventory unavailable — cancel right away */
    @KafkaListener(topics = "inventory-failed", groupId = "order-service-group")
    public void onInventoryFailed(InventoryFailedEvent event) {
        orderService.cancelOrder(event.orderId(), "Inventory: " + event.reason());
    }

    @KafkaListener(topics = "payment-charged", groupId = "order-service-group")
    public void onPaymentCharged(PaymentChargedEvent event) {
        orderService.updateStatus(event.orderId(), OrderStatus.PAYMENT_CHARGED);
    }

    /** Compensating: payment declined — inventory-service already released stock */
    @KafkaListener(topics = "payment-failed", groupId = "order-service-group")
    public void onPaymentFailed(PaymentFailedEvent event) {
        orderService.cancelOrder(event.orderId(), "Payment: " + event.reason());
    }

    @KafkaListener(topics = "order-shipped", groupId = "order-service-group")
    public void onOrderShipped(OrderShippedEvent event) {
        orderService.updateStatus(event.orderId(), OrderStatus.SHIPPED);
        System.out.printf("[order-service] Order %s shipped! Tracking: %s%n",
                event.orderId(), event.trackingNumber());
    }

    /** Compensating: shipping failed — payment-service refunded, inventory-service released stock */
    @KafkaListener(topics = "ship-failed", groupId = "order-service-group")
    public void onShipFailed(ShipFailedEvent event) {
        orderService.cancelOrder(event.orderId(), "Shipping: " + event.reason());
    }
}
