package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyUserRequest(@NotBlank(message = "Por favor insira um email")
                                @Email(message = "Por favor insira um email valido")
                                String email,
                                @NotBlank(message = "Por favor insira uma codigo de verificacao")
                                @Size(min = 6 , max = 6 , message = "Codigo de verificacao deve ter 6 caracteres")
                                String verificationCode) {
}
