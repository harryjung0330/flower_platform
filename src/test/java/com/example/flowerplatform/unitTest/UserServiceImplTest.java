package com.example.flowerplatform.unitTest;


import com.example.flowerplatform.exceptions.OAuth2SaveExternalUserException;
import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.dto.input.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.dto.input.SaveInternalUserServiceDto;
import com.example.flowerplatform.service.implementation.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {UserServiceImpl.class})
@Slf4j
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    public void testSaveExternalUser(){
        String registrationId = "01231203";
        AuthenticationProvider authenticationProvider = AuthenticationProvider.GOOGLE;
        Role role = Role.USER;
        AppUser externalUser;
        AppUser postSaveExternalUserToCompare;

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

        when(userRepository.save(any(AppUser.class))).thenReturn(postSaveExternalUserToCompare);

        SaveExternalUserServiceDto saveExternalUserServiceDto = SaveExternalUserServiceDto
                .builder()
                .authenticationProvider(externalUser.getAuthenticationProvider())
                .registrationId(externalUser.getRegistrationId())
                .role(externalUser.getRole())
                .build();

        AppUser postExternalUser = userService.saveExternalUser(saveExternalUserServiceDto);

        assertEquals(postExternalUser, postSaveExternalUserToCompare);

    }


    @Test
    public void saveExternalUserWithInternalProviderTest(){
        String registrationId = "01231203";
        Role role = Role.USER;

        SaveExternalUserServiceDto dataToTest = SaveExternalUserServiceDto.builder()
                .authenticationProvider(AuthenticationProvider.INTERNAL)
                .role(role)
                .registrationId(registrationId)
                .build();

        assertThrows(OAuth2SaveExternalUserException.class, ()->{
            userService.saveExternalUser(dataToTest);
        });
    }

    @Test
    public void saveInternalUserTest(){
        String email = "harry12312@gmail.com";
        String password = "asdfsfdfds";
        Role role = Role.USER;

        SaveInternalUserServiceDto saveInternalUserServiceDto = SaveInternalUserServiceDto.builder()
                .email(email)
                .rawPassword(password)
                .role(role)
                .build();

        AppUser appUser = AppUser.builder()
                .email(email)
                .role(Role.USER)
                .authenticationProvider(AuthenticationProvider.INTERNAL)
                .password(password)
                .build();

        when(userRepository.save(appUser)).thenReturn(appUser);
        when(passwordEncoder.encode(appUser.getPassword())).thenReturn(appUser.getPassword());

        AppUser postSave = userService.saveInternalUser(saveInternalUserServiceDto);

        assertEquals(postSave, appUser);

    }

}
