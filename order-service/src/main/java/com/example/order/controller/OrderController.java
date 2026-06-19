package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.item(), request.quantity());
    }

    @GetMapping
    public Map<String, Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    public record CreateOrderRequest(String item, int quantity) {}
}
