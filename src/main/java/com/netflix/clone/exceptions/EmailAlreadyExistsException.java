package com.netflix.clone.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String mesaage){
        super(mesaage);
    }
}
