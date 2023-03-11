package com.example.flowerplatform;

import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.tokenManager.Token;
import com.example.flowerplatform.security.authentication.tokenManager.TokenManager;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.oauth2.OAuth2SuccessAndFailureHandlers;
import com.example.flowerplatform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.PrintWriter;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest  //class를 지정하면, ObjectMapper가 LocalTime을 제대로 변환을 못하는 에러때문에, class를 지정하지 않았습니다.
@Slf4j
public class OAuth2SuccessAndFailureHandlersTest
{
    @MockBean
    UserService userService;

    @MockBean
    TokenManager tokenManager;

    @Autowired
    OAuth2SuccessAndFailureHandlers handlers;

    @Test
    @SneakyThrows
    public void testOnSuccessHandler(){
        //given
        Role role = Role.USER;
        String registrationId = "001123123131";
        AuthenticationProvider authenticationProvider = AuthenticationProvider.APPLE;
        Long userId = 1L;

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        OAuth2AuthenticationToken authentication = Mockito.mock(OAuth2AuthenticationToken.class);
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);

        String tokenToReturn = "aaasdf.asdfasdf.asfadfs";

        AppUser appUser = AppUser.builder()
                .id(userId)
                .role(role)
                .registrationId(registrationId)
                .authenticationProvider(authenticationProvider)
                        .build();

        when(authentication.getName()).thenReturn(registrationId);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(authenticationProvider.name());
        when(userService.findByAuthenticationProviderAndRegistrationId(
                any(AuthenticationProvider.class),
                any(String.class)))
                .thenReturn(Optional.of(appUser));

        when(tokenManager.createToken(any(Token.class))).thenReturn(tokenToReturn);
        when(response.getWriter()).thenReturn(printWriter);

        //when
        handlers.oauthSuccessResponse(request, response, authentication);

        //then
        verify(response, times(3)).getWriter();
        verify(tokenManager, times(2)).createToken(any(Token.class));
        verify(userService, times(1)).findByAuthenticationProviderAndRegistrationId(
                any(AuthenticationProvider.class),
                any(String.class));
    }


    @Test
    @SneakyThrows
    public void testFailureHandlers(){
        //given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        PrintWriter printWriter = Mockito.mock(PrintWriter.class);

        AuthenticationException authenticationException = Mockito.mock(AuthenticationException.class);

        when(authenticationException.getMessage()).thenReturn("authentication failed!");
        when(response.getWriter()).thenReturn(printWriter);

        //when
        handlers.oauthFailureResponse(request, response, authenticationException);

        //then

        verify(response, times(3)).getWriter();


    }



}
