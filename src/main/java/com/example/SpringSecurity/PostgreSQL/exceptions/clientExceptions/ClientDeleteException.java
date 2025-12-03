package com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions;

public class ClientDeleteException extends RuntimeException {
    public ClientDeleteException(String message) {
        super(message);
    }
}
