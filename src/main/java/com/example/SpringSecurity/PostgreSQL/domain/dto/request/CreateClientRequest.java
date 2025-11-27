package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(@NotBlank(message = "Nome é obrigatorio") String name,
                                  @NotBlank(message = "email é obrigatorio") String email,
                                  @NotBlank(message = "telefone é obrigatorio") String phone) {
}
