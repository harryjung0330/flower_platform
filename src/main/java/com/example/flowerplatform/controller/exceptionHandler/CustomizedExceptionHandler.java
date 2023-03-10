package com.example.flowerplatform.controller.exceptionHandler;

import com.example.flowerplatform.dto.MessageFormat;
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

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("handleMissingPathVariable is called");

        String message = ex.getMessage();

        MessageFormat exceptionResult =
                MessageFormat.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("handleMissingServletRequestParameter is called");

        String message = ex.getMessage();

        MessageFormat exceptionResult =
                MessageFormat.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("handleMissingServletRequestPart method is called! ");

        String message = ex.getMessage();

        MessageFormat exceptionResult =
                MessageFormat.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){
        log.debug("handleMethodArgumentNotValid is called");

        String rejectedValue = ex.getBindingResult().getFieldError().getRejectedValue() == null? ""
                : ex.getBindingResult().getFieldError().getRejectedValue().toString();

        String message = ex.getBindingResult().getFieldError().getDefaultMessage() == null? ""
                :ex.getBindingResult().getFieldError().getDefaultMessage();

        MessageFormat exceptionResult =
                MessageFormat.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return new ResponseEntity(exceptionResult, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleInternalServerExceptions(Exception ex, WebRequest request){
        MessageFormat exceptionResult = MessageFormat.builder()
                .message("internal server error => " + ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity(exceptionResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
