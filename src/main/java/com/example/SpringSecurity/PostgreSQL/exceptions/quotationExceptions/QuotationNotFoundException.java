package com.example.SpringSecurity.PostgreSQL.exceptions.quotationExceptions;

public class QuotationNotFoundException extends RuntimeException {
    public QuotationNotFoundException(String message) {
        super(message);
    }
}
