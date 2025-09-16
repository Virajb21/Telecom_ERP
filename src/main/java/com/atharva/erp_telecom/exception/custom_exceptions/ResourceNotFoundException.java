package com.atharva.erp_telecom.exception.custom_exceptions;


// Thrown when a resource is not found.
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
