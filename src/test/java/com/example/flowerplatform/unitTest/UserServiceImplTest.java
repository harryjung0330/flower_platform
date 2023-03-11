package com.example.flowerplatform.unitTest;


import com.example.flowerplatform.exceptions.OAuth2SaveExternalUserException;
import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.dto.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.implementation.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;


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

}
