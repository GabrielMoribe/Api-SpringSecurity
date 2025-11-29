package com.example.SpringSecurity.PostgreSQL.exceptions.healthPlanExceptions;

public class HealthPlanRetrievalException extends RuntimeException {
    public HealthPlanRetrievalException(String message) {
        super(message);
    }
}
