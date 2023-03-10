package com.example.flowerplatform.security.authentication.tokenManager.exception;

public class JwtNotFoundException extends RuntimeException{
    public JwtNotFoundException(String msg)
    {
        super(msg);
    }
}
