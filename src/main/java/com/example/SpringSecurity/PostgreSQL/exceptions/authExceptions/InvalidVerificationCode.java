package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class InvalidVerificationCode extends RuntimeException {
    public InvalidVerificationCode(String message) {
        super(message);
    }
}
