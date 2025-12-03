package com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions;

public class ClientUpdateException extends RuntimeException {
    public ClientUpdateException(String message) {
        super(message);
    }
}
