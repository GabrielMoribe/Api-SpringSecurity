package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSubscriptionRequest(
        @NotBlank(message = "O nome do plano é obrigatório")
        String planId,
        String backUrl,
        String testPayerEmail) {}