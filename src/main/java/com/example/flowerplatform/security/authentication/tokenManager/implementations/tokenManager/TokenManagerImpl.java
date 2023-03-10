package com.example.flowerplatform.security.authentication.tokenManager.implementations.tokenManager;

import com.example.flowerplatform.security.authentication.tokenManager.Token;
import com.example.flowerplatform.security.authentication.tokenManager.TokenManager;
import com.example.flowerplatform.security.authentication.tokenManager.TokenWorker;
import com.example.flowerplatform.security.authentication.tokenManager.exception.TokenTypeNotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenManagerImpl implements TokenManager
{
    private final List<TokenWorker> tokenWorkerList;


    @Override
    public String createToken(Token token) {

        for(TokenWorker tokenWorker: tokenWorkerList)
        {
            if(tokenWorker.supports(token.getClass()))
            {
              return tokenWorker.createToken(token);
            }
        }

        throw new TokenTypeNotSupportedException("Token cannot be created because the token class " + token.getClass().toString() + " is not supported!");
    }

    @Override
    public <T extends Token> T verifyToken(String token, Class<T> tokenClass) {
        for(TokenWorker tokenWorker: tokenWorkerList)
        {
            if(tokenWorker.supports(tokenClass))
            {
                return tokenWorker.verifyToken(token, tokenClass);
            }
        }

        throw new TokenTypeNotSupportedException("Token cannot be verified because the token class " + token.getClass().toString() + " is not supported!");
    }

    @Override
    public <T extends Token> T readToken(String token, Class<T> tokenClass) {
        for(TokenWorker tokenWorker: tokenWorkerList)
        {
            if(tokenWorker.supports(tokenClass))
            {
                return tokenWorker.readToken(token, tokenClass);
            }
        }

        throw new TokenTypeNotSupportedException("Token cannot be read because the token class " + token.getClass().toString() + " is not supported!");
    }
}
