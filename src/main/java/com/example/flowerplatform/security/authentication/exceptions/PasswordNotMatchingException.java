package com.example.flowerplatform.security.authentication.exceptions;

import org.springframework.security.core.AuthenticationException;

public class PasswordNotMatchingException extends AuthenticationException
{
    public PasswordNotMatchingException(String msg)
    {
        super(msg);
    }
}
