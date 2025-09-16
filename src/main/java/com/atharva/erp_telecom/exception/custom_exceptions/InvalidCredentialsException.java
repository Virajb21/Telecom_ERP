package com.atharva.erp_telecom.exception.custom_exceptions;

// Thrown when the credentials the user has entered don't match.
public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
