package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final PaymentService paymentService;
    public WebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<ApiResponse<String>> mercadopago(@RequestParam Map<String , String> params){
        String id = params.get("id");
        String topic = params.get("topic");

        if("payment".equals(topic) || "payment".equals(params.get("type"))){
            if(id == null){
                id = params.get("id");
            }
            if(id != null){
                paymentService.processPaymentNotification(Long.parseLong(id));
            }
        }
        ApiResponse<String> response = ApiResponse.success("Notificacao recebida com sucesso");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
