package com.gamary.paymentservice.service;


import com.gamary.paymentplatformcommons.dto.PaymentDTO;
import com.gamary.paymentplatformcommons.dto.PaymentEventNotification;

public interface PaymentService {

    void receivePayment(PaymentEventNotification paymentEventNotification);
}
