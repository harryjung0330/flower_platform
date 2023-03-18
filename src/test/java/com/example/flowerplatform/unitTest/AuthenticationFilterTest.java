package com.example.flowerplatform.unitTest;

import com.example.flowerplatform.security.authentication.dto.LoginRequestDto;
import com.example.flowerplatform.security.authentication.exceptions.UnsupportedAuthenticationTypeException;
import com.example.flowerplatform.security.authentication.filter.AuthenticationFilter;
import com.example.flowerplatform.security.securityConfig.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@SpringBootTest(classes = {AuthenticationFilter.class})
@Slf4j
public class AuthenticationFilterTest {
    @Autowired
    AbstractAuthenticationProcessingFilter filter;

    @MockBean
    RequestMatcher requestMatcher;

    @MockBean
    ObjectMapper objectMapper;

    @MockBean
    Validator validator;

    @MockBean
    AuthenticationManager authenticationManager;

    @Test
    public void testClassType(){
        assertThat(filter.getClass().equals(AuthenticationFilter.class));
    }

    @Test
    @SneakyThrows
    public void attemptSuccessAuthenticationTest(){
        //data
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setContextPath(SecurityConfig.LOGIN_PATH);
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setContent(new byte[]{});


        LoginRequestDto loginRequestDto = new LoginRequestDto("aaaa", "aaaa");
        Authentication authenticationToReturn = new UsernamePasswordAuthenticationToken(
                "hello", null, List.of());

        when(objectMapper.readValue( request.getReader(), LoginRequestDto.class))
                .thenReturn(loginRequestDto);
        when(validator.validate(any(LoginRequestDto.class))).thenReturn(new HashSet<>());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToReturn);

        //when
        Authentication authenticationResult = filter.attemptAuthentication(request, response);

        //then
        assertEquals(authenticationToReturn, authenticationResult);

    }

    @Test
    @SneakyThrows
    public void attemptWrongMethodAuthenticationTest(){
        //data
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setContextPath(SecurityConfig.LOGIN_PATH);
        request.setMethod(HttpMethod.GET.name());
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setContent(new byte[]{});


        LoginRequestDto loginRequestDto = new LoginRequestDto("aaaa", "aaaa");
        Authentication authenticationToReturn = new UsernamePasswordAuthenticationToken(
                "hello", null, List.of());

        when(objectMapper.readValue( request.getReader(), LoginRequestDto.class))
                .thenReturn(loginRequestDto);
        when(validator.validate(any(LoginRequestDto.class))).thenReturn(new HashSet<>());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToReturn);

        //when
        assertThrows(UnsupportedAuthenticationTypeException.class,
                () -> {Authentication authenticationResult = filter.attemptAuthentication(request, response);}
        );




    }
    @Test
    @SneakyThrows
    public void attemptObjectMapperFailureAuthenticationTest(){
        //data
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setContextPath(SecurityConfig.LOGIN_PATH);
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setContent(new byte[]{});


        LoginRequestDto loginRequestDto = new LoginRequestDto("aaaa", "aaaa");
        Authentication authenticationToReturn = new UsernamePasswordAuthenticationToken(
                "hello", null, List.of());

        when(objectMapper.readValue( request.getReader(), LoginRequestDto.class))
                .thenThrow(new IOException());
        when(validator.validate(any(LoginRequestDto.class))).thenReturn(new HashSet<>());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToReturn);

        //when
        assertThrows(AuthenticationServiceException.class,
                () -> {Authentication authenticationResult = filter.attemptAuthentication(request, response);}
        );

    }

}