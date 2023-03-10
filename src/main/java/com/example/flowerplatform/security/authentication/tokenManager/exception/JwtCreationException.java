package com.example.flowerplatform.security.authentication.tokenManager.exception;

public class JwtCreationException extends RuntimeException {
   public JwtCreationException(String msg)
   {
       super(msg);
   }
}
