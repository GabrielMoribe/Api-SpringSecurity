package com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
