package com.example.flowerplatform.security.authentication.tokenManager.implementations.tokenWorkers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.security.authentication.exceptions.UnsupportedTokenTypeException;
import com.example.flowerplatform.security.authentication.tokenManager.Token;
import com.example.flowerplatform.security.authentication.tokenManager.TokenWorker;
import com.example.flowerplatform.security.authentication.tokenManager.exception.JwtCreationException;
import com.example.flowerplatform.security.authentication.tokenManager.exception.JwtMalformedException;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.token.JwtRefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRefreshTokenWorker implements TokenWorker
{
    private final SessionRepository sessionRepository;
    private static final String ROLE = "role";

    private static final String SESSION_ID = "session_id";

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
    @Transactional
    public String createToken(Token token) {
        if(!supports(token.getClass()))
            throw new UnsupportedTokenTypeException("JwtRefreshTokenWorker does not support " + token.getClass());


        JwtRefreshToken jwtRefreshToken = (JwtRefreshToken) token;

        if(jwtRefreshToken.getUserId() == null)
            throw new JwtCreationException("cannot create token because userId is null!");

        Session session = Session.builder()
                .userId(jwtRefreshToken.getUserId())
                .build();

        Session postCreation = sessionRepository.save(session);

        String refreshToken = createAuthenticationToken(jwtRefreshToken.getSubject(), jwtRefreshToken.getCreatedAt(), jwtRefreshToken.getExpiresAt(), jwtRefreshToken.getRole()
                , postCreation.getSessionId(), jwtRefreshToken.getUserId());

        Session sessionToUpdate = Session.builder()
                .sessionId(postCreation.getSessionId())
                .userId(postCreation.getUserId())
                .expiresAt(jwtRefreshToken.getExpiresAt())
                .refreshToken(refreshToken)
                .build();

        Session r = sessionRepository.save(sessionToUpdate);

        log.debug("saved session in db: " + r.toString());

        return refreshToken;
    }

    @Override
    public <T extends Token> T verifyToken(String token, Class<T> tokenClass) {
        if(!supports(tokenClass))
            throw new UnsupportedTokenTypeException("JwtRefreshTokenWorker does not support " + token.getClass());

        DecodedJWT decodedJWT = decodeJwtToken(token);
        return (T) convertToToken(decodedJWT);
    }

    @Override
    public <T extends Token> T readToken(String token, Class<T> tokenClass) {
        if(!supports(tokenClass))
            throw new UnsupportedTokenTypeException("JwtRefreshTokenWorker does not support " + token.getClass());

        DecodedJWT decodedJWT = readJwtToken(token);
        return (T) convertToToken(decodedJWT);
    }

    private JwtRefreshToken convertToToken(DecodedJWT decodedJWT){
        String subject;
        String role;
        Date expiresAt;
        Date createdAt;
        long sessionId;
        long userId;

        try {
            subject = decodedJWT.getSubject();
            role = decodedJWT.getClaim(ROLE).asString();
            expiresAt = decodedJWT.getExpiresAt();
            createdAt = decodedJWT.getIssuedAt();
            sessionId = decodedJWT.getClaim(SESSION_ID).asLong();
            userId = decodedJWT.getClaim(USER_ID).asLong();

        }
        catch (Exception e)
        {
            log.error("error occurred while verifying token");
            log.error(e.toString());

            throw new JwtMalformedException("the decoded jwt token does not contain necessary fields: "
                    + decodedJWT.toString());
        }
        return JwtRefreshToken.builder()
                .subject(subject)
                .role(role)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .sessionId(sessionId)
                .userId(userId)
                .build();
    }

    @Override
    public <T extends Token> boolean supports(Class<T> tokenClass) {
        if(tokenClass.equals(JwtRefreshToken.class))
            return true;
        return false;
    }

    private String createAuthenticationToken(String subject, Date issuedAt, Date expiresAt, String role, long sessionId, long userId) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withClaim(ROLE, role)
                .withClaim(SESSION_ID, sessionId)
                .withClaim(USER_ID, userId)
                .sign(Algorithm.HMAC512(SECRET_KEY));

    }

    private DecodedJWT decodeJwtToken(String jwtToken)
    {
        DecodedJWT result;

        try {
            result = JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(jwtToken);
        }
        catch (JWTVerificationException jwtVerificationException)
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
