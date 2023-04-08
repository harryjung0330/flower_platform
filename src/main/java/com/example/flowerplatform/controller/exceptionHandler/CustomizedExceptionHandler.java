package com.example.flowerplatform.controller.exceptionHandler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.service.exceptions.UnusableSessionException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Date;

@ControllerAdvice
@RestController
@Slf4j
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler
{

    private static final String REFRESH_TOKEN_PATH = "/users/tokens";
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("handleMissingPathVariable is called");

        String message = ex.getMessage();

        MessageFormat exceptionResult = getMessageFormat(
                message,
                HttpStatus.BAD_REQUEST.value(),
                null);

        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("handleMissingServletRequestParameter is called");

        String message = ex.getMessage();

        MessageFormat exceptionResult = getMessageFormat(
                        message,
                        HttpStatus.BAD_REQUEST.value(),
                        null);

        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("handleMissingServletRequestPart method is called! ");

        String message = ex.getMessage();

        MessageFormat exceptionResult = getMessageFormat(
                message,
                HttpStatus.BAD_REQUEST.value(),
                null);


        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){
        log.debug("handleMethodArgumentNotValid is called");

        String rejectedValue = ex.getBindingResult().getFieldError().getRejectedValue() == null? ""
                : ex.getBindingResult().getFieldError().getRejectedValue().toString();

        String message = ex.getBindingResult().getFieldError().getDefaultMessage() == null? ""
                :ex.getBindingResult().getFieldError().getDefaultMessage();

        MessageFormat exceptionResult = getMessageFormat(
                message,
                HttpStatus.BAD_REQUEST.value(),
                null);


        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleInternalServerExceptions(Exception ex, WebRequest request){
        MessageFormat exceptionResult = getMessageFormat(
                "internal server error => " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null);

        return new ResponseEntity(exceptionResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity<Object> handleTokenExpiredException(Exception ex, WebRequest request){

        String path = request.getContextPath();
        String message;
        int status;

        if(path.equals(REFRESH_TOKEN_PATH))
        {
            status = HttpStatus.UNAUTHORIZED.value();
            message = "refresh token is expired with detailed message => " + ex.getMessage();
        }
        else{
            status = HttpStatus.UNAUTHORIZED.value();
            message = "token is expired! with detailed message => " + ex.getMessage();
        }

        MessageFormat exceptionResult = getMessageFormat(
                message,
                status,
                null);

        return new ResponseEntity(exceptionResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public final ResponseEntity<Object> handleJWTVerificationException(Exception ex, WebRequest request){

        String path = request.getContextPath();
        String message;
        int status;

        if(path.equals(REFRESH_TOKEN_PATH))
        {
            status = HttpStatus.UNAUTHORIZED.value();
            message = "Jwt verification with message => " + ex.getMessage();
        }
        else{
            status = HttpStatus.UNAUTHORIZED.value();
            message = "token is expired! with detailed message => " + ex.getMessage();
        }

        MessageFormat exceptionResult = getMessageFormat(
                message,
                status,
                null);

        return new ResponseEntity(exceptionResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler( UnusableSessionException.class)
    public final ResponseEntity<Object> handleUnusableSessionException(Exception ex, WebRequest request){

        String path = request.getContextPath();
        String message;
        int status;

        if(path.equals(REFRESH_TOKEN_PATH))
        {
            status = HttpStatus.UNAUTHORIZED.value();
            message = "cannot use rotated refresh token =>" + ex.getMessage();
        }
        else{
            status = HttpStatus.UNAUTHORIZED.value();
            message = "unable to use the session =>" + ex.getMessage();
        }

        MessageFormat exceptionResult = getMessageFormat(
                message,
                status,
                null);

        return new ResponseEntity(exceptionResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private <T> MessageFormat<T>  getMessageFormat(String message, int status, T data){
        MessageFormat exceptionResult = MessageFormat.builder()
                .message(message)
                .status(status)
                .data(data)
                .timestamp(new Date())
                .build();

        return exceptionResult;
    }


}
