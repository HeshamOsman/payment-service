package com.gamary.paymentservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gamary.paymentplatformcommons.dto.PaymentEventNotification;
import com.gamary.paymentplatformcommons.dto.ProcessedPaymentRequestDTO;
import com.gamary.paymentservice.service.PaymentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final KafkaProducer kafkaProducer;

    private static final String APP_TOKEN_HEADER = "X-App-Token";
    private static final String API_KEY_HEADER = "API-Key";
    private static final String PAYMENT_API_URL = "/paymentrequests/{paymentRequestToken}/payments/{paymentToken}";
    @Value("${Tikkie.appToken}")
    private String appToken;
    @Value("${Tikkie.apiKey}")
    private String apiKey;
    @Value("${Tikkie.baseUrl}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public PaymentServiceImpl(KafkaProducer kafkaProducer, RestTemplate restTemplate, ObjectMapper mapper) {
        this.kafkaProducer = kafkaProducer;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    @Override
    public void receivePayment(PaymentEventNotification paymentEventNotification) {
        try {
            ObjectNode jsonResponse = (ObjectNode)mapper.readTree(callTikkieApi(paymentEventNotification));
            jsonResponse.put("paymentRequestToken",paymentEventNotification.getPaymentRequestToken());
            kafkaProducer.sendMessage(jsonResponse.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String callTikkieApi(PaymentEventNotification paymentEventNotification) throws JsonProcessingException {

        var headersEntity = new HttpEntity<ProcessedPaymentRequestDTO>(getRequestHeaders());
        return restTemplate.exchange(baseUrl+PAYMENT_API_URL, HttpMethod.GET,headersEntity, String.class,
                paymentEventNotification.getPaymentRequestToken(),paymentEventNotification.getPaymentToken()).getBody();

    }

    private HttpHeaders getRequestHeaders(){
        var headers = new HttpHeaders();
        headers.add(APP_TOKEN_HEADER,appToken);
        headers.add(API_KEY_HEADER,apiKey);
        return headers;
    }

}
