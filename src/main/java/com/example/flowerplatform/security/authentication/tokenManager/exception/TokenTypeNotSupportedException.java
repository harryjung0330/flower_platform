package com.example.flowerplatform.security.authentication.tokenManager.exception;

public class TokenTypeNotSupportedException extends RuntimeException
{
    public TokenTypeNotSupportedException(String msg)
    {
        super(msg);
    }
}
