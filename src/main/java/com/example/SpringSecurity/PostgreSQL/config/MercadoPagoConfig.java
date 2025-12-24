package com.example.SpringSecurity.PostgreSQL.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MercadoPagoConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.webhook-secret:}")
    private String webhookSecret;

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;
}