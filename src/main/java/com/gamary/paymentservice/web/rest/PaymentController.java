package com.gamary.paymentservice.web.rest;

import com.gamary.paymentplatformcommons.dto.PaymentEventNotification;
import com.gamary.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> receivePayment(@RequestBody PaymentEventNotification paymentEventNotification){
        paymentService.receivePayment(paymentEventNotification);
        return ResponseEntity.ok().build();
    }

}
