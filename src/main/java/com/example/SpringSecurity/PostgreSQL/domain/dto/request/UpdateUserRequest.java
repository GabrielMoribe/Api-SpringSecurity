package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(@NotBlank(message = "Por favor insira um nome")String name ,
                                @NotBlank(message = "Por favor insira um email")String email) {

}
