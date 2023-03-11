package com.example.flowerplatform;

import com.example.flowerplatform.security.authentication.authenticationManager.CustomAuthenticationManager;
import com.example.flowerplatform.security.authentication.authenticationProvider.EmailAuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CustomAuthenticationManagerTest
{
    AuthenticationManager authenticationManager;

    @Autowired
    List<AuthenticationProvider> authenticationProviders;

    EmailAuthenticationProvider emailAuthenticationProvider;

    //data used for testing.
    Authentication authentication;

    Authentication postAuthentication;

    private void initializeMockInstances(){
        log.debug("======================= called initializedMockInstances!");

        emailAuthenticationProvider = Mockito.mock(EmailAuthenticationProvider.class);

        Mockito.when(emailAuthenticationProvider.supports(authentication.getClass())).thenReturn(true);
        Mockito.when(emailAuthenticationProvider.authenticate(authentication)).thenReturn(postAuthentication);


        //find EmailAuthenticationProvider instance and replace it with mock!
        for(int i = 0; i < authenticationProviders.size(); i++)
        {
            if(authenticationProviders.get(i).getClass().equals(EmailAuthenticationProvider.class))
            {
                log.debug("found emailAuthenticationProvider!");
                authenticationProviders.set(i, emailAuthenticationProvider);
                break;
            }
        }


    }

    private void initializeDataBeforeTest(){
        String email = "harryjung0330@gmail.com";
        String password = "hello";
        Role role = Role.USER;

        authentication = new UsernamePasswordAuthenticationToken(email, password);

        postAuthentication = new UsernamePasswordAuthenticationToken(email, null ,
                List.of(new SimpleGrantedAuthority(role.toString())));
    }

    @Before
    public void beforeTest()
    {
        initializeDataBeforeTest();
        initializeMockInstances();

        log.debug("================= size of authenticationProviders: " + authenticationProviders.size());


        for(int i = 0; i < authenticationProviders.size(); i++)
        {
            log.debug("====================" + authenticationProviders.get(i).getClass().toString());
        }

        emailAuthenticationProvider.authenticate(authentication);

        log.debug("======================= initialize authentication manager");
        authenticationManager = new CustomAuthenticationManager(authenticationProviders);

    }


    @Test
    public void testCustomAuthenticationManager()
    {
        assertEquals(authenticationManager.authenticate(authentication), postAuthentication);
    }



}
