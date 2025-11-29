package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }
}
