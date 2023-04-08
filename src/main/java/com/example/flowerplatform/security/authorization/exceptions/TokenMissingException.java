package com.example.flowerplatform.security.authorization.exceptions;

public class TokenMissingException extends RuntimeException
{
    public TokenMissingException(String msg)
    {
        super(msg);
    }
}
