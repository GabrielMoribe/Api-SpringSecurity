package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegUserRequest(@NotBlank(message = "Nome é obrigatorio")
                             @Size(min = 3 , max = 20 , message = "Nome deve ter entre 3 e 20 caracteres")
                             String name ,
                             @NotBlank(message = "Email é obrigatorio")
                             @Email(message = "email deve ser valido")
                             String email ,
                             @NotBlank(message = "Senha é obrigatoria")
                             @Size(min=6 , message = "Senha deve ter no minimo 6 caracteres")
                             String password) {
}
