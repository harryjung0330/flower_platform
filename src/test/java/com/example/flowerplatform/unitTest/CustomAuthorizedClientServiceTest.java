package com.example.flowerplatform.unitTest;


import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.oauth2.CustomAuthorizedClientService;
import com.example.flowerplatform.service.UserService;
import com.example.flowerplatform.service.dto.input.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
@SpringBootTest
public class CustomAuthorizedClientServiceTest
{
    private UserService userService;

    private CustomAuthorizedClientService customAuthorizedClientService;





    private void initDependency(){
        userService = Mockito.mock(UserService.class);


        customAuthorizedClientService = new CustomAuthorizedClientService(userService);
    }

    @BeforeEach
    public void beforeTest(){
        initDependency();
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

        Mockito.when(userService.saveExternalUser(saveExternalUserServiceDto)).thenReturn(AppUser.builder()
                .id(1L)
                .authenticationProvider(saveExternalUserServiceDto.getAuthenticationProvider())
                .registrationId(saveExternalUserServiceDto.getRegistrationId())
                .role(saveExternalUserServiceDto.getRole())
                .build());

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

        Mockito.when(userService.saveExternalUser(duplicateExternalUserServiceDto))
                .thenThrow(new DuplicateUserException("Duplicate User!"));

        customAuthorizedClientService.saveAuthorizedClient(duplicateAuthorizedClient, principal);
    }



}
