package com.example.SpringSecurity.PostgreSQL.exceptions.refreshTokenExceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
