package com.example.flowerplatform.unitTest;


import com.example.flowerplatform.exceptions.OAuth2SaveExternalUserException;
import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.dto.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.implementation.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceImplTest {

    private UserServiceImpl userService;

    private UserRepository userRepository;

    //testData
    private AppUser externalUser;

    private AppUser postSaveExternalUserToCompare;


    private void initTestData(){
        String registrationId = "01231203";
        AuthenticationProvider authenticationProvider = AuthenticationProvider.GOOGLE;
        Role role = Role.USER;

        postSaveExternalUserToCompare = AppUser.builder()
                .id(1L)
                .role(role)
                .authenticationProvider(authenticationProvider)
                .registrationId(registrationId)
                .build();

        externalUser = AppUser.builder()
                .registrationId(registrationId)
                .authenticationProvider(authenticationProvider)
                .role(role)
                .build();

    }

    private void initializeMock(){
        userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.save(externalUser)).thenReturn(postSaveExternalUserToCompare);

    }

    @Before
    public void beforeTest(){
        initTestData();
        initializeMock();
        userService = new UserServiceImpl(userRepository);

    }


    @Test
    public void testSaveExternalUser(){
        SaveExternalUserServiceDto saveExternalUserServiceDto = SaveExternalUserServiceDto
                .builder()
                .authenticationProvider(externalUser.getAuthenticationProvider())
                .registrationId(externalUser.getRegistrationId())
                .role(externalUser.getRole())
                .build();

        AppUser postExternalUser = userService.saveExternalUser(saveExternalUserServiceDto);

        assertEquals(postExternalUser, postSaveExternalUserToCompare);

    }

    @Test(expected = OAuth2SaveExternalUserException.class)
    public void saveExternalUserWithInternalProviderTest(){
        SaveExternalUserServiceDto dataToTest = SaveExternalUserServiceDto.builder()
                .authenticationProvider(AuthenticationProvider.INTERNAL)
                .role(Role.USER)
                .registrationId("12312412412")
                .build();

        userService.saveExternalUser(dataToTest);
    }

}
