package com.example.flowerplatform.util.tokenManager;

public interface TokenWorker
{
    String createToken(Token token);

    <T extends Token> T verifyToken(String token, Class<T> tokenClass);

    <T extends Token> T readToken(String token, Class<T> tokenClass);

    <T extends Token> boolean supports(Class<T> tokenClass);
}
