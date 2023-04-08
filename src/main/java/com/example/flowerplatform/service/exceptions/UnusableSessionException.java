package com.example.flowerplatform.service.exceptions;

public class UnusableSessionException extends RuntimeException
{
    public UnusableSessionException(String msg)
    {
        super(msg);
    }
}
