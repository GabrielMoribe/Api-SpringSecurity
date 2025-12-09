package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificatioCodeRequest(
        @NotBlank(message = "Email é obrigatorio")
        @Email(message = "insira um email valido")
        String email) {
}
