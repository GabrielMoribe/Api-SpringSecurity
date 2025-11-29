package com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions;

public class ClientCreationException extends RuntimeException {
    public ClientCreationException(String message) {
        super(message);
    }
}
