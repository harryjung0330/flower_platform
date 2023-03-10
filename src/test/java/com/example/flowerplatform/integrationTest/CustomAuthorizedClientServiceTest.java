package com.example.flowerplatform.integrationTest;

import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.oauth2.CustomAuthorizedClientService;
import com.example.flowerplatform.service.dto.SaveExternalUserServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@IfProfileValue(name ="spring.profiles.active", value ="dev")
@Slf4j
public class CustomAuthorizedClientServiceTest
{
    @Autowired
    CustomAuthorizedClientService customAuthorizedClientService;

    @Autowired
    UserRepository userRepository;


    @Before
    public void beforeEachTest(){
        userRepository.deleteAll();
    }

    @Test
    public void testSaveOauth2Client(){
        //saveExternalUserServiceDto 생성하기
        SaveExternalUserServiceDto saveExternalUserServiceDto = SaveExternalUserServiceDto.builder()
                .role(Role.REGISTRATION_NOT_COMPLETE_USER)
                .authenticationProvider(AuthenticationProvider.GOOGLE)
                .registrationId("10029901990231")
                .build();

        //principal 생성하기
        Authentication principal = Mockito.mock(Authentication.class);

        //authorizedClient 생성하기
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);

        Mockito.when(clientRegistration.getClientId()).thenReturn("70001231231231231");
        Mockito.when(clientRegistration.getRegistrationId()).thenReturn("google");
        Mockito.when(clientRegistration.getClientSecret()).thenReturn("asssdfasdfasdfasa");

        OAuth2AccessToken auth2AuthenticationToken = Mockito.mock(OAuth2AccessToken.class);


        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(clientRegistration, saveExternalUserServiceDto.getRegistrationId(), auth2AuthenticationToken);


        customAuthorizedClientService.saveAuthorizedClient(authorizedClient, principal );
    }

    @Test
    public void testDuplicateItem(){
        //principal 생성하기
        Authentication principal = Mockito.mock(Authentication.class);

        //authorizedClient 생성하기
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        Mockito.when(clientRegistration.getClientId()).thenReturn("70001231231231231");
        Mockito.when(clientRegistration.getRegistrationId()).thenReturn("google");
        Mockito.when(clientRegistration.getClientSecret()).thenReturn("asssdfasdfasdfasa");

        SaveExternalUserServiceDto duplicateExternalUserServiceDto = SaveExternalUserServiceDto.builder()
                .role(Role.REGISTRATION_NOT_COMPLETE_USER)
                .authenticationProvider(AuthenticationProvider.GOOGLE)
                .registrationId("1002990141233123")
                .build();

        OAuth2AccessToken auth2AuthenticationToken = Mockito.mock(OAuth2AccessToken.class);

        OAuth2AuthorizedClient duplicateAuthorizedClient = new OAuth2AuthorizedClient(clientRegistration, duplicateExternalUserServiceDto.getRegistrationId(), auth2AuthenticationToken);

        customAuthorizedClientService.saveAuthorizedClient(duplicateAuthorizedClient, principal);
    }

    @After
    public void afterTest(){
        userRepository.deleteAll();
    }



}
