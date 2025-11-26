package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegUserRequest(@NotBlank(message = "Nome é obrigatorio") String name ,
                             @NotBlank(message = "Email é obrigatorio") String email ,
                             @NotBlank(message = "Senha é obrigatoria") String password) {
}
