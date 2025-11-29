package com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String message){
        super(message);
    }
}