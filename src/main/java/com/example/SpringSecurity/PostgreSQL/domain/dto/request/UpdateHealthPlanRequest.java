package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;


public record UpdateHealthPlanRequest(@NotBlank(message = "O nome do plano é obrigatório") String name,
                                      @NotBlank(message = "A operadora é obrigatória") String operator,
                                      @NotBlank(message = "O código da operadora é obrigatório") String operatorCode,

                                      @NotNull( message = "O preço base é obrigatório")
                                      @Positive(message = "O preço base deve ser maior que zero")
                                      BigDecimal basePrice,

                                      @NotBlank(message = "A cobertura é obrigatória") String coverage
                                      ) {
}
