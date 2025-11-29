package com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
