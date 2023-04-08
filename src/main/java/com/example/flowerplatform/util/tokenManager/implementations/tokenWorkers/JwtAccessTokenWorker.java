package com.example.flowerplatform.util.tokenManager.implementations.tokenWorkers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.flowerplatform.security.authentication.exceptions.UnsupportedTokenTypeException;
import com.example.flowerplatform.util.tokenManager.Token;
import com.example.flowerplatform.util.tokenManager.TokenWorker;
import com.example.flowerplatform.util.tokenManager.exception.JwtMalformedException;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenWorker implements TokenWorker
{

    private static final String ROLE = "role";

    private static final String USER_ID = "user_id";

    @Value("${jwt.token.secretKey.primary}")
    private String SECRET_KEY;

    @Value("${jwt.token.secretKey.secondary}")
    private String SECRET_KEY_SECONDARY;

    public void setPrimarySecretKey(String secretKey){
        SECRET_KEY = secretKey;
    }

    public void setSecondarySecretKey(String secretKey){
        SECRET_KEY_SECONDARY = secretKey;
    }

    @Override
    public String createToken(Token token) {
        if(!supports(token.getClass()))
            throw new UnsupportedTokenTypeException("JwtAccessTokenWorker does not support " + token.getClass());
        JwtAccessToken jwtAccessToken = (JwtAccessToken) token;

        if(jwtAccessToken.getCreatedAt() == null || jwtAccessToken.getExpiresAt() == null) {

            jwtAccessToken.setCreatedAt(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(jwtAccessToken.getCreatedAt());
            calendar.add(Calendar.MINUTE, TokenProperties.ACCESS_TOKEN_DURATION_MIN);

            jwtAccessToken.setExpiresAt(calendar.getTime());
        }

        return createAuthenticationToken(jwtAccessToken.getSubject(), jwtAccessToken.getCreatedAt(), jwtAccessToken.getExpiresAt(),
                jwtAccessToken.getRole(), jwtAccessToken.getUserId());
    }

    @Override
    public <T extends Token> T verifyToken(String token, Class<T> tokenClass) {
        if(!supports(tokenClass))
            throw new UnsupportedTokenTypeException("JwtAccessTokenWorker does not support " + token.getClass());

        DecodedJWT decodedJWT = decodeJwtToken(token);
        return (T) convertToToken(decodedJWT);
    }

    @Override
    public <T extends Token> T readToken(String token, Class<T> tokenClass) {
        if(!supports(tokenClass))
            throw new UnsupportedTokenTypeException("JwtAccessTokenWorker does not support " + token.getClass());

        DecodedJWT decodedJWT = readJwtToken(token);
        return (T) convertToToken(decodedJWT);
    }

    @Override
    public <T extends Token> boolean supports(Class<T> tokenClass) {
        if(tokenClass.equals(JwtAccessToken.class))
            return true;
        return false;
    }

    private String createAuthenticationToken(String subject, Date issuedAt, Date expiresAt, String role, Long userId) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withClaim(USER_ID, userId)
                .withClaim(ROLE, role)
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    //convert DecodedJwt to JwtAccessToken
    private JwtAccessToken convertToToken(DecodedJWT decodedJWT){
        String subject;
        String role;
        Date expiresAt;
        Date createdAt;
        Long userId;

        try {
            subject = decodedJWT.getSubject();
            role = decodedJWT.getClaim(ROLE).asString();
            expiresAt = decodedJWT.getExpiresAt();
            createdAt = decodedJWT.getIssuedAt();
            userId = decodedJWT.getClaim(USER_ID).asLong();

        }
        catch (Exception e)
        {
            log.error("error occurred while verifying token");
            log.error(e.toString());

            throw new JwtMalformedException("the decoded jwt token does not contain necessary fields: "
                    + decodedJWT.toString());
        }
        return JwtAccessToken.builder()
                .subject(subject)
                .role(role)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .userId(userId)
                .build();
    }

    private DecodedJWT decodeJwtToken(String jwtToken)
    {
        DecodedJWT result;

        try {
            result = JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(jwtToken);
        }
        catch (SignatureVerificationException signitureVerificationException)
        {
            log.debug("jwt verification with primary key failed! it will try the secondary key!");
            result = JWT.require(Algorithm.HMAC512(SECRET_KEY_SECONDARY)).build().verify(jwtToken);
        }

        return result;
    }

    private DecodedJWT readJwtToken(String jwtToken)
    {
        return JWT.decode(jwtToken);
    }
}
