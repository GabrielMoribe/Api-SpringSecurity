package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank(message = "Email é obrigatorio")
                           @Email(message = "email deve ser valido")
                           String email ,
                           @NotBlank(message = "Senha é obrigatoria")
                           @Size(min=6 , message = "Senha deve ter no minimo 6 caracteres")
                           String password) {
}
