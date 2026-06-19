package com.example.payment.controller;

import com.example.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Toggle payment failure simulation: POST /payment/fail/true or /payment/fail/false */
    @PostMapping("/fail/{enabled}")
    public String setFailMode(@PathVariable boolean enabled) {
        paymentService.setSimulateFailure(enabled);
        return "Payment failure simulation set to: " + enabled;
    }
}
