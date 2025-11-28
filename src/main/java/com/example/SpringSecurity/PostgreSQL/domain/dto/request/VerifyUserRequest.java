package com.example.SpringSecurity.PostgreSQL.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyUserRequest(@NotBlank(message = "Por favor insira um email") String email,
                                @NotBlank(message = "Por favor insira uma codigo de verificacao") String verificationCode) {
}
