package com.example.SpringSecurity.PostgreSQL.domain.dto.response;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserResponse(String name , String email) {
}
