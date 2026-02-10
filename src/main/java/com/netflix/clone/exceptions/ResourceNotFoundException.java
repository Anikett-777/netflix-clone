package com.netflix.clone.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String mesaage){
        super(mesaage);
    }
}
