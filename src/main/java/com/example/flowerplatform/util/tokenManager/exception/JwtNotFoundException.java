package com.example.flowerplatform.util.tokenManager.exception;

public class JwtNotFoundException extends RuntimeException{
    public JwtNotFoundException(String msg)
    {
        super(msg);
    }
}
