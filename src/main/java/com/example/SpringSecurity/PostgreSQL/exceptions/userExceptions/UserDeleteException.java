package com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions;

public class UserDeleteException extends RuntimeException {
    public UserDeleteException(String message) {
        super(message);
    }
}
