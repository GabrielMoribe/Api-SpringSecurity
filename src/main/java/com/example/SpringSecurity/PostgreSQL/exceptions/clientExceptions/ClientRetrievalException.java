package com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions;

public class ClientRetrievalException extends RuntimeException {
    public ClientRetrievalException(String message) {
        super(message);
    }
}
