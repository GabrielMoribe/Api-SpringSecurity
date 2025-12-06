package com.example.SpringSecurity.PostgreSQL.exceptions.healthPlanExceptions;

public class HealthPlanNotFoundException extends RuntimeException {
    public HealthPlanNotFoundException(String message) {
        super(message);
    }
}
