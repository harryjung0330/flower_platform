package com.example.flowerplatform.security.authentication.exceptions;


import org.springframework.security.core.AuthenticationException;

public class UnsupportedTokenTypeException extends AuthenticationException
{
    public UnsupportedTokenTypeException(String msg)
    {
        super(msg);
    }
}
