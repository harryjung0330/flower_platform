package com.example.flowerplatform.util.tokenManager.exception;

public class JwtCreationException extends RuntimeException {
   public JwtCreationException(String msg)
   {
       super(msg);
   }
}
