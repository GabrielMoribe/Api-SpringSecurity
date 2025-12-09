package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
