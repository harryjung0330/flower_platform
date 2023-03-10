package com.example.flowerplatform.security.authentication.handlers;

import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authentication.dto.TokenDto;
import com.example.flowerplatform.security.authentication.tokenManager.TokenManagerImplt;
import com.example.flowerplatform.security.authentication.userDetails.AppUserDetails;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnSuccessAuthenticationHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    private final TokenManagerImplt tokenManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.debug("onAuthenticationSuccess() called!");
        log.debug("authentication: " + authentication.toString());

        AppUserDetails appUserDetails = (AppUserDetails) authentication.getDetails();

        log.debug("appUserDetails: " + appUserDetails.toString());

        String access_token =  tokenManager.createAuthenticationAccessToken(appUserDetails.getUserId(), appUserDetails.getRole());
        String refresh_token = tokenManager.createAuthenticationRefreshToken(appUserDetails.getUserId(), appUserDetails.getRole());

        TokenDto tokenDto = new TokenDto(access_token, refresh_token, appUserDetails.getUserId());
        MessageFormat loginSuccessMessage =
                MessageFormat.builder()
                                .message("login success!")
                                .timestamp(LocalDateTime.now())
                                .data(tokenDto)
                                .build();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        objectMapper.writeValue(response.getWriter(), loginSuccessMessage);

        response.flushBuffer();
    }
}
