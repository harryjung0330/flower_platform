package com.example.flowerplatform.security.authentication.tokenManager;

import com.example.flowerplatform.security.authentication.tokenManager.dto.VerifyTokenResultDto;
import com.example.flowerplatform.security.authentication.tokenManager.exception.JwtMalformedException;
import com.example.flowerplatform.security.authentication.tokenManager.exception.JwtNotFoundException;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@AllArgsConstructor
public class TokenManagerImplt {

    private final TokenCreator tokenCreator;

    private final TokenVerifier tokenVerifier;

    public String createAuthenticationAccessToken(long user_id, Role role){
        return TokenProperties.HEADER_PREFIX + tokenCreator.createAuthenticationAccessToken(user_id, role);
    }

    public String createAuthenticationRefreshToken(long user_id, Role role){
        return TokenProperties.HEADER_PREFIX + tokenCreator.createAuthenticationRefreshToken(user_id, role);
    }

    public VerifyTokenResultDto verifyToken(String jwtToken)
    {
        String extractedToken = extract(jwtToken);
        VerifyTokenResultDto verifyTokenResultDto = tokenVerifier.verifyToken(extractedToken);

        if(!verifyTokenResultDto.isValid())
        {
            throw new JwtMalformedException("the token: " +jwtToken + " is malformed!");
        }

        return verifyTokenResultDto;
    }

    //for signup and password ------------------------------------------------------------------------------------
    //return token for next step

    public String createSignUpCreateCodeToken(String email, String code)
    {
        return tokenCreator.createSignUpSendMailToken(email,code);
    }

    public String createPasswordCreateCodeToken(String email, String code)
    {
        return tokenCreator.createPasswordSendMailToken(email,code);
    }


    // verifying and creating tokens for password and signup after checking code ----------------------------------------
    //return new token for next step

    public String verifySignUpCodeToken(String token, String email, String code)
    {
        String verifiedEmail = tokenVerifier.verifySignUpCheckCodeToken(token, email, code);

        return tokenCreator.createSignUpCheckCodeToken(verifiedEmail);
    }

    public String verifyPasswordCodeToken(String token, String email, String code)
    {
        String verifiedEmail = tokenVerifier.verifyPasswordCheckCodeToken(token, email, code);

        return tokenCreator.createPasswordCheckCodeToken(verifiedEmail);
    }

    //verifying tokens for password and signup before changing resources ---------------------------------------
    //return nothing. if no error, it means success!

    public void verifySignUpAfterCodeToken(String token, String email)
    {
        tokenVerifier.verifySignUpAfterCodeToken(token, email);
    }

    public void verifyPasswordAfterCodeToken(String token, String email)
    {
        tokenVerifier.verifyPasswordAfterCodeToken(token, email);
    }

    //---------------private methods ---------------------------------------------------------------------

    private String extract(String jwtTokenHeader) {
        if(!StringUtils.hasText(jwtTokenHeader)){
            throw new JwtNotFoundException("Authorization header cannot be blank!");
        }

        if(jwtTokenHeader.length() < TokenProperties.HEADER_PREFIX.length()){
            throw new JwtNotFoundException("Invalid authorization header size");
        }

        if(!jwtTokenHeader.startsWith(TokenProperties.HEADER_PREFIX)){
            throw new JwtNotFoundException("Invalid token format");
        }

        return jwtTokenHeader.substring(TokenProperties.HEADER_PREFIX.length());
    }

}
