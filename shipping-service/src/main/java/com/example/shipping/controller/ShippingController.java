package com.example.shipping.controller;

import com.example.shipping.service.ShippingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    /** Toggle shipping failure simulation: POST /shipping/fail/true or /shipping/fail/false */
    @PostMapping("/fail/{enabled}")
    public String setFailMode(@PathVariable boolean enabled) {
        shippingService.setSimulateFailure(enabled);
        return "Shipping failure simulation set to: " + enabled;
    }
}
