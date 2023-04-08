package com.example.flowerplatform.security.authentication.handlers;

import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authentication.dto.TokenDto;
import com.example.flowerplatform.util.tokenManager.Token;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtRefreshToken;
import com.example.flowerplatform.security.authentication.userDetails.AppUserDetails;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnSuccessAuthenticationHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    private final TokenManager tokenManager;


    //create access token and refresh token when authenticated successfully!
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.debug("onAuthenticationSuccess() called!");
        log.debug("authentication: " + authentication.toString());

        AppUserDetails appUserDetails = (AppUserDetails) authentication.getDetails();

        log.debug("appUserDetails: " + appUserDetails.toString());

        Token accessToken = createAccessToken(appUserDetails.getUserId(), appUserDetails.getRole());
        Token refreshToken = createRefreshToken(appUserDetails.getUserId(), appUserDetails.getRole());

        String access_token =  tokenManager.createToken(accessToken);
        String refresh_token = tokenManager.createToken(refreshToken);

        TokenDto tokenDto = new TokenDto(access_token, refresh_token, appUserDetails.getUserId());
        MessageFormat loginSuccessMessage =
                MessageFormat.builder()
                                .message("login success!")
                                .timestamp(new Date())
                                .data(tokenDto)
                                .build();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        objectMapper.writeValue(response.getWriter(), loginSuccessMessage);

        response.flushBuffer();
    }

    //create access token
    private Token createAccessToken(Long userId, Role role)
    {
        Date cur = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cur);
        calendar.add(Calendar.MINUTE, TokenProperties.ACCESS_TOKEN_DURATION_MIN);

        Date expiresAt = calendar.getTime();

        return JwtAccessToken.builder()
                .userId(userId)
                .role(role.name())
                .subject(String.valueOf(userId))
                .createdAt(cur)
                .expiresAt(expiresAt)
                .build();
    }

    //create refresh token!

    private Token createRefreshToken(Long userId, Role role)
    {
        Date cur = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cur);
        calendar.add(Calendar.MINUTE, TokenProperties.REFRESH_TOKEN_DURATION_MIN);

        Date expiresAt = calendar.getTime();

        return JwtRefreshToken.builder()
                .userId(userId)
                .role(role.name())
                .subject(String.valueOf(userId))
                .createdAt(cur)
                .expiresAt(expiresAt)
                .build();
    }
}
