package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @NotBlank(message = "Email é obrigatorio")
        @Email(message = "email deve ser valido")
        String newEmail) {
}
