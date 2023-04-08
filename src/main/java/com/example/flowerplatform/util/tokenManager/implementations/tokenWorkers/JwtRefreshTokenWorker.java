package com.example.flowerplatform.util.tokenManager.implementations.tokenWorkers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.security.authentication.exceptions.UnsupportedTokenTypeException;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.util.tokenManager.Token;
import com.example.flowerplatform.util.tokenManager.TokenWorker;
import com.example.flowerplatform.util.tokenManager.exception.JwtCreationException;
import com.example.flowerplatform.util.tokenManager.exception.JwtMalformedException;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtRefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
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


    @Override
    @Transactional
    public String createToken(Token token) throws JwtCreationException{
        if(!supports(token.getClass()))
            throw new UnsupportedTokenTypeException("JwtRefreshTokenWorker does not support " + token.getClass());


        JwtRefreshToken jwtRefreshToken = (JwtRefreshToken) token;

        if(jwtRefreshToken.getUserId() == null || jwtRefreshToken.getSubject() == null)
            throw new JwtCreationException("cannot create token because userId is null or subject is null!");

        if(jwtRefreshToken.getCreatedAt() == null || jwtRefreshToken.getExpiresAt() == null) {
            log.debug("=========================================== created date or expiresAt is null!");

            jwtRefreshToken.setCreatedAt(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(jwtRefreshToken.getCreatedAt());
            calendar.add(Calendar.MINUTE, TokenProperties.REFRESH_TOKEN_DURATION_MIN);

            jwtRefreshToken.setExpiresAt(calendar.getTime());
        }

        if(jwtRefreshToken.getSessionId() != null) {
            //token session id가 있을경우 -> 토큰을 refresh하는 경우!

            log.debug("================================== session id is not null!");
            log.debug("================================== session id is " + jwtRefreshToken.getSessionId());

            String refreshToken = createAuthenticationToken(jwtRefreshToken.getSubject(), jwtRefreshToken.getCreatedAt(), jwtRefreshToken.getExpiresAt(), jwtRefreshToken.getRole()
                    , jwtRefreshToken.getSessionId(), jwtRefreshToken.getUserId());

            updateSession(jwtRefreshToken.getSessionId(),jwtRefreshToken.getUserId(), jwtRefreshToken.getExpiresAt(), refreshToken );

            return refreshToken;
        }

        else {
            //token session id가 없을경우 -> session을 아예 새로 시작하는 경우!

            log.debug("================================== session id is null!");

            Session session = Session.builder()
                    .userId(jwtRefreshToken.getUserId())
                    .build();

            Session postCreation = sessionRepository.save(session);

            String refreshToken = createAuthenticationToken(jwtRefreshToken.getSubject(), jwtRefreshToken.getCreatedAt(), jwtRefreshToken.getExpiresAt(), jwtRefreshToken.getRole()
                    , postCreation.getSessionId(), jwtRefreshToken.getUserId());


            Session r = updateSession(postCreation.getSessionId(), postCreation.getUserId(), jwtRefreshToken.getExpiresAt(), refreshToken);

            log.debug("saved session in db: " + r.toString());

            return refreshToken;
        }
    }

    private Session updateSession(Long sessionId, Long userId, Date expiresAt, String refreshToken){
        Session sessionToUpdate = Session.builder()
                .sessionId(sessionId)
                .userId(userId)
                .expiresAt(expiresAt)
                .refreshToken(refreshToken)
                .build();

        return sessionRepository.save(sessionToUpdate);

    }


    @Override
    public <T extends Token> T verifyToken(String token, Class<T> tokenClass) throws UnsupportedTokenTypeException, JwtMalformedException, JWTVerificationException{
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
        catch (SignatureVerificationException sgException)
        {
            log.debug("jwt verification with primary key failed! it will try the secondary key!");
            log.debug("error message: " + sgException.getMessage());
            result = JWT.require(Algorithm.HMAC512(SECRET_KEY_SECONDARY)).build().verify(jwtToken);
        }

        return result;
    }

    private DecodedJWT readJwtToken(String jwtToken)
    {
        return JWT.decode(jwtToken);
    }
}
