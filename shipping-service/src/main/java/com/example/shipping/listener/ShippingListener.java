package com.example.shipping.listener;

import com.example.events.PaymentChargedEvent;
import com.example.shipping.service.ShippingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingListener {

    private final ShippingService shippingService;

    public ShippingListener(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    /** Step 3: payment charged — ship the order */
    @KafkaListener(topics = "payment-charged", groupId = "shipping-service-group")
    public void onPaymentCharged(PaymentChargedEvent event) {
        shippingService.ship(event.orderId());
    }
}
