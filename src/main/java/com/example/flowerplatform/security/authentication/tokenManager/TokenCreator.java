package com.example.flowerplatform.security.authentication.tokenManager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import com.example.flowerplatform.security.authentication.tokenManager.exception.JwtCreationException;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
class TokenCreator {
    @Value("${jwt.token.secretKey.primary}")
    private String SECRET_KEY;

    private final PasswordEncoder passwordEncoder;

    public String createAuthenticationAccessToken(long user_id, Role role )
    {
        return createAuthenticationToken(user_id, role, TokenProperties.ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createAuthenticationRefreshToken(long user_id, Role role)
    {
        return createAuthenticationToken(user_id, role, TokenProperties.REFRESH_TOKEN_EXPIRED_TIME);
    }

    public String createSignUpSendMailToken(String email, String code)
    {
        if(email == null || code == null)
        {
            log.error("email or encoded code is null! (in createSignUpSendMailToken)!");
            throw new JwtCreationException("cannot create code token because email or code is null!");
        }
        return createCodeToken(email, passwordEncoder.encode(code), 0, false,
                TokenProperties.SIGNUP_SEND_REQUEST_TOKEN_EXPIRED_TIME);
    }

    public String createSignUpCheckCodeToken(String email)
    {
        if(email == null )
        {
            log.error("email is null! (in createSignCheckCodeToken)!");
            throw new JwtCreationException("cannot create code token because email is null!");
        }
        return createCodeToken(email, null, 0, true,
                TokenProperties.SIGNUP_CHECK_CODE_TOKEN_EXPIRED_TIME);
    }

    public String createPasswordSendMailToken(String email, String code)
    {
        if(email == null || code == null)
        {
            log.error("email or encoded code is null! (in createPasswordSendMailToken)!");
            throw new JwtCreationException("cannot create code token because email or code is null!");
        }
        return createCodeToken(email, passwordEncoder.encode(code), 1, false,
                TokenProperties.PASSWORD_SEND_REQUEST_TOKEN_EXPIRED_TIME);
    }

    public String createPasswordCheckCodeToken(String email)
    {
        if(email == null )
        {
            log.error("email is null! (in createSignCheckCodeToken)!");
            throw new JwtCreationException("cannot create code token because email is null!");
        }
        return createCodeToken(email, null, 1, true,
                TokenProperties.PASSWORD_CHECK_CODE_TOKEN_EXPIRED_TIME);
    }


    //--------------------------------------- private methods -------------------------------------------------------
    private String createAuthenticationToken(long user_id, Role role, long validPeriod) {
        return JWT.create()
                .withSubject(String.valueOf(user_id))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + validPeriod))
                .withClaim(TokenProperties.ROLE, role.toString())
                .sign(Algorithm.HMAC512(SECRET_KEY));

    }

    private String createCodeToken(String email, String encoded_code  ,int purpose_code, boolean is_verified, long validPeriod)
    {
        JWTCreator.Builder builder = JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + validPeriod))
                .withClaim(TokenProperties.EMAIL, email)
                .withClaim(TokenProperties.PURPOSE_CODE, purpose_code)
                .withClaim(TokenProperties.IS_VERIFIED, is_verified);

        if(encoded_code != null)
        {

            builder = builder.withClaim(TokenProperties.CODE, encoded_code);
        }

        return builder.sign(Algorithm.HMAC512(SECRET_KEY));
    }


}
