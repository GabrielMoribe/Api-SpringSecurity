package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class UserAlreadyVerified extends RuntimeException {
    public UserAlreadyVerified(String message) {
        super(message);
    }
}
