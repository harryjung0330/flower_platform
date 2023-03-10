package com.example.flowerplatform.service.exceptions;

//exception class for saving existing user
public class DuplicateUserException extends RuntimeException{
    public DuplicateUserException(String msg)
    {
        super(msg);
    }
}
