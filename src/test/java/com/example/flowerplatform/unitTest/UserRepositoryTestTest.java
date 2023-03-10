package com.example.flowerplatform.unitTest;

import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
@IfProfileValue(name ="spring.profiles.active", value ="dev")
@Slf4j
public class UserRepositoryTestTest
{
    @Autowired
    UserRepository userRepository;

    //data to test saving users
    AppUser externalUser;

    AppUser internalUser;


    private void refreshUserRepository(){
        userRepository.deleteAll();
    }

    private void initializeSaveUserData(){
        externalUser = AppUser.builder()
                .registrationId("011111123")
                .authenticationProvider(AuthenticationProvider.GOOGLE)
                .role(Role.USER)
                .build();

        internalUser = AppUser.builder()
                .email("randomUser@gmail.com")
                .password("helloWorld!")
                .build();


    }

    @Before
    public void beforeTest(){
        refreshUserRepository();
        initializeSaveUserData();
    }


    @Test
    public void testSaveExternalUser(){
        AppUser postSaveExternalUser = userRepository.save(externalUser);

        assertEquals(postSaveExternalUser.getRole(), externalUser.getRole());
        assertEquals(postSaveExternalUser.getRegistrationId(), externalUser.getRegistrationId());
        assertEquals(postSaveExternalUser.getAuthenticationProvider(), externalUser.getAuthenticationProvider());
        assertEquals(postSaveExternalUser.getPassword(), externalUser.getPassword());
        assertEquals(postSaveExternalUser.getEmail(), externalUser.getEmail());


    }

    @Test
    public void testFindByRegistrationIdAndAuthProvider(){
        AppUser postInsert = userRepository.save(externalUser);

        AppUser retrievedUser = userRepository.findByRegistrationIdAndAuthenticationProvider(
                externalUser.getRegistrationId(),
                externalUser.getAuthenticationProvider()
            ).get();

        assertEquals(retrievedUser, postInsert);

    }

    @Test
    public void testSaveInternalUser(){
        AppUser postSaveInternalUser = userRepository.save(internalUser);

        assertEquals(postSaveInternalUser.getEmail(), internalUser.getEmail());
        assertEquals(postSaveInternalUser.getRegistrationId(), internalUser.getRegistrationId());
        assertEquals(postSaveInternalUser.getPassword(), internalUser.getPassword());
        assertEquals(postSaveInternalUser.getRole(), internalUser.getRole());
        assertEquals(postSaveInternalUser.getAuthenticationProvider(), internalUser.getAuthenticationProvider());
    }



    @After
    public void afterTest(){

        refreshUserRepository();
    }

}
