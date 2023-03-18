package com.example.flowerplatform.integrationTest;

import com.example.flowerplatform.exceptions.OAuth2SaveExternalUserException;
import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.dto.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import com.example.flowerplatform.service.implementation.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@IfProfileValue(name ="spring.profiles.active", value ="dev")
@Slf4j
public class UserServiceImplTest
{
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userServiceImpl;


    @BeforeEach
    public void beforeEachTest(){
        userRepository.deleteAll();
    }


    private void saveExternalUserAndTest(SaveExternalUserServiceDto saveExternalUserServiceDto){

        AppUser appUser = userServiceImpl.saveExternalUser(saveExternalUserServiceDto);

        assertEquals(saveExternalUserServiceDto.getRole(), appUser.getRole());
        assertEquals(saveExternalUserServiceDto.getRegistrationId(), appUser.getRegistrationId());
        assertEquals(saveExternalUserServiceDto.getAuthenticationProvider(), appUser.getAuthenticationProvider());

    }

    @Test
    public void saveExternalUserTest()
    {
        SaveExternalUserServiceDto saveExternalUserServiceDto = SaveExternalUserServiceDto
                .builder()
                .authenticationProvider(AuthenticationProvider.GOOGLE)
                .registrationId("111121312312")
                .role(Role.USER)
                .build();

        saveExternalUserAndTest(saveExternalUserServiceDto);
    }

    @Test
    public void saveMultipleExternalUsers(){
        List<SaveExternalUserServiceDto> dtoList = List.of(
                SaveExternalUserServiceDto.builder()
                        .authenticationProvider(AuthenticationProvider.GOOGLE)
                        .role(Role.USER)
                        .registrationId("12312412412")
                        .build(),
                SaveExternalUserServiceDto.builder()
                        .authenticationProvider(AuthenticationProvider.APPLE)
                        .role(Role.REGISTRATION_NOT_COMPLETE_USER)
                        .registrationId("123124124114")
                        .build(),
                SaveExternalUserServiceDto.builder()
                        .authenticationProvider(AuthenticationProvider.GOOGLE)
                        .role(Role.REGISTRATION_NOT_COMPLETE_USER)
                        .registrationId("123124124114")
                        .build(),
                SaveExternalUserServiceDto.builder()
                        .authenticationProvider(AuthenticationProvider.GOOGLE)
                        .role(Role.ADMIN)
                        .registrationId("1231241251")
                        .build()
        );

        for(SaveExternalUserServiceDto s: dtoList)
        {
            saveExternalUserAndTest(s);
        }
    }


    @Test
    public void saveDuplicateUserTest(){
        SaveExternalUserServiceDto dataToTest = SaveExternalUserServiceDto.builder()
                .authenticationProvider(AuthenticationProvider.GOOGLE)
                .role(Role.USER)
                .registrationId("12312412412")
                .build();

        assertThrows(DuplicateUserException.class, () -> {
            for(int i = 0; i < 2; i++) {
                saveExternalUserAndTest(dataToTest);
            }
        });

    }

    @Test
    public void saveExternalUserWithInternalProviderTest(){
        SaveExternalUserServiceDto dataToTest = SaveExternalUserServiceDto.builder()
                .authenticationProvider(AuthenticationProvider.INTERNAL)
                .role(Role.USER)
                .registrationId("12312412412")
                .build();

        assertThrows(
                OAuth2SaveExternalUserException.class,
                ()->{
                    saveExternalUserAndTest(dataToTest);
                }
        );

    }

    @AfterEach
    public void afterTest(){
        userRepository.deleteAll();
    }


}
