package com.example.flowerplatform.security.authentication.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedAuthenticationTypeException extends AuthenticationException {
    public UnsupportedAuthenticationTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnsupportedAuthenticationTypeException(String msg)
    {
        super(msg);
    }
}
