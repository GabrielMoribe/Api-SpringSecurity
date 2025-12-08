package com.example.SpringSecurity.PostgreSQL.exceptions.refreshTokenExceptions;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
