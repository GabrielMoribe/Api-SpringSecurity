package com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions;

public class UserUpdateException extends RuntimeException {
    public UserUpdateException(String message) {
        super(message);
    }
}
