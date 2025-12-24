package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateSubscriptionRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.SubscriptionResponse;
import com.example.SpringSecurity.PostgreSQL.service.SubscriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse subscriptionResponse = subscriptionService.createSubscription(request);
        ApiResponse<SubscriptionResponse> response = ApiResponse.success(subscriptionResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getStatus() {
        SubscriptionResponse subscriptionResponse = subscriptionService.getSubscriptionStatus();
        ApiResponse<SubscriptionResponse> response = ApiResponse.success(subscriptionResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription() {
        SubscriptionResponse subscriptionResponse = subscriptionService.cancelSubscription();
        ApiResponse<SubscriptionResponse> response = ApiResponse.success(subscriptionResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Webhook do Mercado Pago
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestBody(required = false) Map<String, Object> body) {

        subscriptionService.processWebhook(type, dataId, body);
        ApiResponse<String> response = ApiResponse.success("Webhook processed");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Callback do Mercado Pago
    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<Map<String, String>>> handleCallback(
            @RequestParam("preapproval_id") String preapprovalId,
            @RequestParam(value = "status", required = false) String status) {
        Map<String, String> response = subscriptionService.processCallback(preapprovalId, status);
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success(response);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/test-payment")
    public ResponseEntity<Map<String, Object>> testPayment() {
        Map<String, Object> preference = new HashMap<>();
        preference.put("items", java.util.List.of(
                Map.of(
                        "title", "Teste",
                        "quantity", 1,
                        "unit_price", 10.00,
                        "currency_id", "BRL"
                )
        ));
        preference.put("payer", Map.of(
                "email", "test_user_6252916341832847704@testuser.com"
        ));

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth("APP_USR-8356450068465266-121611-f440e256f9934e78e889dcc3f597b244-3061310941");

        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        var response = restTemplate.exchange(
                "https://api.mercadopago.com/checkout/preferences",
                org.springframework.http.HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(preference, headers),
                Map.class
        );

        return ResponseEntity.ok(response.getBody());
    }
}


