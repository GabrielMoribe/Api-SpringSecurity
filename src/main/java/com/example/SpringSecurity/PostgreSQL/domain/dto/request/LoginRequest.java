package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(@NotBlank(message = "Email é obrigatorio") String email ,
                           @NotBlank(message = "Senha é obrigatoria") String password) {
}
