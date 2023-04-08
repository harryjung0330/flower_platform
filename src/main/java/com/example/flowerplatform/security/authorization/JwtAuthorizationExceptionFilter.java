package com.example.flowerplatform.security.authorization;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authorization.exceptions.TokenMissingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        log.debug("uri in first filter: " + request.getRequestURI());

        try{
            chain.doFilter(request, response);
        }
        catch (TokenMissingException e){
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e.getMessage(),"토큰이 없습니다.");
        }
        catch(TokenExpiredException e){
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage(),"토큰이 만료되었습니다.");
        } catch(SignatureVerificationException e){
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage(),"부적절한 토큰입니다.");
        } catch(JWTVerificationException e){
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage(),"유효하지 않은 토큰입니다.");
        } catch (Exception e){
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage(), "서버 내부의 에러가 났습니다.");
        }

    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, String errorMessage, String details) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());


        MessageFormat errorMsgDto = MessageFormat.builder()
                .status(status.value())
                .message(errorMessage + " => " + details)
                .data(null)
                .timestamp(new Date())
                .build();

        objectMapper.writeValue(response.getWriter(), errorMsgDto);
    }
}
