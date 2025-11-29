package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class ExpiredVerificationCodeException extends RuntimeException {
    public ExpiredVerificationCodeException(String message) {
        super(message);
    }
}
