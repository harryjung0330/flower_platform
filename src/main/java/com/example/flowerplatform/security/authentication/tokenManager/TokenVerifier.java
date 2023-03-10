package com.example.flowerplatform.security.authentication.tokenManager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.flowerplatform.security.authentication.tokenManager.dto.VerifyTokenResultDto;
import com.example.flowerplatform.security.authentication.tokenManager.exception.JwtMalformedException;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
class TokenVerifier
{
    @Value("${jwt.token.secretKey.primary}")
    private String SECRET_KEY;

    private final PasswordEncoder passwordEncoder;

    public String verifySignUpCheckCodeToken(String jwtToken, String email, String code)
    {
        return verifyCheckCodeToken(jwtToken, code, email, 0);
    }

    public String verifyPasswordCheckCodeToken(String jwtToken,  String email, String code)
    {
        return verifyCheckCodeToken(jwtToken, code, email, 1);
    }

    public String verifySignUpAfterCodeToken(String jwtToken, String email)
    {
        return verifyAfterCodeToken(jwtToken, email, 0);
    }

    public String verifyPasswordAfterCodeToken(String jwtToken, String email)
    {
        return verifyAfterCodeToken(jwtToken, email, 1);
    }



    private String verifyCheckCodeToken(String jwtToken, String code, String email, int correctPurposeCode)
    {
        log.debug("called verifyCheckCodeToken()!");
        log.debug("JwtToken: " + jwtToken + " code: " + code + " email: " + email);

        if(jwtToken == null || code == null || email == null)
        {
            throw new JWTVerificationException("cannot verify token because code, token, or email is null!");
        }

        DecodedJWT decodedJWT = decodeJwtToken(jwtToken);

        String parsedEmail = Optional.of(decodedJWT.getClaim(TokenProperties.EMAIL).asString())
                .orElseThrow(() -> new JwtMalformedException("email is missing in token"));
        String parsedCode = Optional.of(decodedJWT.getClaim(TokenProperties.CODE).asString())
                .orElseThrow(() -> new JwtMalformedException("code is missing in token"));
        boolean parsedIsVerified = Optional.of(decodedJWT.getClaim(TokenProperties.IS_VERIFIED).asBoolean())
                .orElseThrow(() -> new JwtMalformedException("is_verified is missing in token"));
        int parsedPurposeCode =  Optional.of(decodedJWT.getClaim(TokenProperties.PURPOSE_CODE).asInt())
            .orElseThrow(() -> new JwtMalformedException("purpose_code is missing in token"));

        if(!parsedEmail.equals(email))
        {
            throw new JWTVerificationException("email is not matching!");
        }

        if(parsedPurposeCode != correctPurposeCode)
        {
            throw new JWTVerificationException("purpose code is not " + String.valueOf(correctPurposeCode));
        }

        if(!passwordEncoder.matches(code, parsedCode))
        {
            throw new JWTVerificationException("code is not matching!");
        }


        return parsedEmail;

    }

    private String verifyAfterCodeToken(String jwtToken,  String email, int correctPurposeCode)
    {
        log.debug("called verifySignUpAfterCodeToken()!");
        log.debug("JwtToken: " + jwtToken +  " email: " + email);

        if(jwtToken == null  || email == null)
        {
            throw new JWTVerificationException("cannot verify token because token, or email is null!");
        }

        DecodedJWT decodedJWT = decodeJwtToken(jwtToken);

        String parsedEmail = Optional.of(decodedJWT.getClaim(TokenProperties.EMAIL).asString())
                .orElseThrow(() -> new JwtMalformedException("email is missing in token"));
        boolean parsedIsVerified = Optional.of(decodedJWT.getClaim(TokenProperties.IS_VERIFIED).asBoolean())
                .orElseThrow(() -> new JwtMalformedException("is_verified is missing in token"));
        int parsedPurposeCode =  Optional.of(decodedJWT.getClaim(TokenProperties.PURPOSE_CODE).asInt())
                .orElseThrow(() -> new JwtMalformedException("purpose_code is missing in token"));

        if(!parsedEmail.equals(email))
        {
            throw new JWTVerificationException("email is not matching!");
        }

        if(parsedPurposeCode != correctPurposeCode)
        {
            throw new JWTVerificationException("purpose code is not " + String.valueOf(correctPurposeCode));
        }

        if(!parsedIsVerified)
        {
            throw new JWTVerificationException("is_verified is not true!");
        }


        return parsedEmail;

    }



    //verify token before accessing protected resources
    public VerifyTokenResultDto verifyToken(String jwtToken)
    {
        DecodedJWT decodedJWT = decodeJwtToken(jwtToken);
        long user_id;
        Role role;

        try {
            user_id = Long.valueOf(decodedJWT.getSubject());
            role = Enum.valueOf(Role.class, decodedJWT.getClaim(TokenProperties.ROLE).asString());
        }
        catch (Exception e)
        {
            log.error("error occurred while verifying token");
            log.error(e.toString());

            throw new JwtMalformedException("the decoded jwt token does not contain either of user id or role: "
                    + decodedJWT.toString());
        }

        return new VerifyTokenResultDto(user_id, role);

    }

    private DecodedJWT decodeJwtToken(String jwtToken)
    {
        return JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(jwtToken);
    }

}
