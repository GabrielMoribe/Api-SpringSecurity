package com.example.SpringSecurity.PostgreSQL.exceptions.quotationExceptions;

public class QuotationCreationException extends RuntimeException {
    public QuotationCreationException(String message) {
        super(message);
    }
}
