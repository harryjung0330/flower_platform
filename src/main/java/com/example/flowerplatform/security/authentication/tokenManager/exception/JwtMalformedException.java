package com.example.flowerplatform.security.authentication.tokenManager.exception;

public class JwtMalformedException extends RuntimeException{
    public JwtMalformedException(String msg)
    {
        super(msg);
    }
}
