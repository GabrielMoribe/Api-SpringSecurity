package com.example.SpringSecurity.PostgreSQL.config;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {
}
