package com.example.flowerplatform.security.authentication.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedRequestException extends AuthenticationException {
    public UnsupportedRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnsupportedRequestException(String msg) {
        super(msg);
    }
}
