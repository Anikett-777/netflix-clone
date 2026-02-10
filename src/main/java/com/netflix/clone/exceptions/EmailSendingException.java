package com.netflix.clone.exceptions;

public class EmailSendingException extends RuntimeException{
    public EmailSendingException(String mesaage,Throwable cause){
        super(mesaage,cause);
    }
}
