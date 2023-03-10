package com.example.flowerplatform.util.tokenManager;

public interface TokenManager
{
    String createToken(Token token);

    <T extends Token> T verifyToken(String token, Class<T> tokenClass);

    <T extends Token> T readToken(String token, Class<T> tokenClass);
}
