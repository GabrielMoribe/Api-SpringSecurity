package com.example.SpringSecurity.PostgreSQL.exceptions.quotationExceptions;

public class InvalidAgeRangeException extends RuntimeException {
    public InvalidAgeRangeException(String message) {
        super(message);
    }
}
