package com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions;

public class UserRetrievalException extends RuntimeException {
    public UserRetrievalException(String message) {
        super(message);
    }
}
