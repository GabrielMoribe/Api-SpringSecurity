package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(@NotBlank(message = "Nome é obrigatorio")
                                  @Size(min = 3, max = 20, message = "Nome deve ter entre 3 e 20 caracteres")
                                  String name,
                                  @NotBlank(message = "email é obrigatorio")
                                  @Email(message = "email deve ser valido")
                                  String email,
                                  @NotBlank(message = "telefone é obrigatorio")
                                  String phone) {
}
