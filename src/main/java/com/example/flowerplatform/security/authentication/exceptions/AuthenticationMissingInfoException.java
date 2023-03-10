package com.example.flowerplatform.security.authentication.exceptions;


import org.springframework.security.core.AuthenticationException;

public class AuthenticationMissingInfoException extends AuthenticationException
{

    public AuthenticationMissingInfoException(String msg)
    {
        super(msg);
    }
}
