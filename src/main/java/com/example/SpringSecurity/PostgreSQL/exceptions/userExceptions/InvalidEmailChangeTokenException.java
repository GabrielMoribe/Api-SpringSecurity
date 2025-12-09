package com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions;

public class InvalidEmailChangeTokenException extends RuntimeException {
    public InvalidEmailChangeTokenException(String message) {
        super(message);
    }
}
