package com.example.order.service;

import com.example.events.OrderCancelledEvent;
import com.example.events.OrderCreatedEvent;
import com.example.order.model.Order;
import com.example.order.model.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public OrderService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /** Entry point: create an order and kick off the saga. */
    public Order createOrder(String item, int quantity) {
        var order = new Order(UUID.randomUUID().toString(), item, quantity, OrderStatus.PENDING);
        orders.put(order.getId(), order);
        kafkaTemplate.send("order-created", order.getId(),
                new OrderCreatedEvent(order.getId(), item, quantity));
        System.out.printf("[order-service] Order %s CREATED (%d x %s)%n",
                order.getId(), quantity, item);
        return order;
    }

    /** Called by the saga listener when a downstream step succeeds. */
    public void updateStatus(String orderId, OrderStatus status) {
        var order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
            System.out.printf("[order-service] Order %s → %s%n", orderId, status);
        }
    }

    /** Compensating action: mark cancelled and notify downstream. */
    public void cancelOrder(String orderId, String reason) {
        updateStatus(orderId, OrderStatus.CANCELLED);
        kafkaTemplate.send("order-cancelled", orderId, new OrderCancelledEvent(orderId, reason));
        System.out.printf("[order-service] Order %s CANCELLED — %s%n", orderId, reason);
    }

    public Map<String, Order> getAllOrders() { return orders; }
}
