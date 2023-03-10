package com.example.flowerplatform.security.authentication.handlers;


import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authentication.exceptions.PasswordNotMatchingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnFailureAuthenticationHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        log.debug("onAuthenticationFailure called!");

        if(exception instanceof PasswordNotMatchingException){
            setErrorResponse(HttpStatus.BAD_REQUEST, response,  "비밀번호가 맞지 않습니다." ,exception.getMessage(), 400);
        } else if(exception instanceof UsernameNotFoundException){
            setErrorResponse(HttpStatus.NOT_FOUND, response, "존재하지 않는 아이디 입니다.", exception.getMessage(), 400 );
        } else if(exception instanceof AuthenticationException){
            setErrorResponse(HttpStatus.BAD_REQUEST, response, "인증 에러", exception.getMessage(), 400);
        } else if(exception instanceof AuthenticationServiceException){
            setErrorResponse(HttpStatus.BAD_REQUEST, response, "부적절한 요청입니다.", exception.getMessage(), 400);
        } else {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, "서버에 문제가 있습니다.", exception.getMessage(), 400);
        }

    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, String errorMessage, String details, int errorCode) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        MessageFormat tempErrorMsgDto = MessageFormat.builder()
                .status(status.value())
                .data(null)
                .message(errorMessage + " => " + details)
                .timestamp(LocalDateTime.now())
                .build();

        /*
        MessageFormat messageFormat = MessageFormat.builder()
                        .data(details)
                        .message(errorMessage)
                        .timestamp(new Date())
                        .build();


         */
        objectMapper.writeValue(response.getWriter(),tempErrorMsgDto );

        response.flushBuffer();
    }
}
