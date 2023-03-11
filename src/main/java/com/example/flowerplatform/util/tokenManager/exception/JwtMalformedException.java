package com.example.flowerplatform.util.tokenManager.exception;

public class JwtMalformedException extends RuntimeException{
    public JwtMalformedException(String msg)
    {
        super(msg);
    }
}
