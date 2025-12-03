package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;

public record QuotationRequest(@NotNull(message = "O ID do cliente é obrigatório")
                               @Positive(message = "O ID do cliente deve ser válido")
                               Long clientId,

                               @NotNull(message = "O ID do plano de saúde é obrigatório")
                               @Positive(message = "O ID do plano deve ser válido")
                               Long healthPlanId,

                               @NotNull(message = "Informe a quantidade de beneficiários por faixa etária")
                               Map<String, Integer> beneficiariesByAge) {
}
