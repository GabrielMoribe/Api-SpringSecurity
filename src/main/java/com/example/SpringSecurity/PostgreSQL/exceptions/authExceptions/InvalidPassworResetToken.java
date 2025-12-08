package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class InvalidPassworResetToken extends RuntimeException {
    public InvalidPassworResetToken(String message) {
        super(message);
    }
}
