package com.example.flowerplatform;

import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.security.authentication.authenticationProvider.EmailAuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.AppUserDetails;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailAuthenticationProviderTest
{
    AuthenticationProvider authenticationProvider;


    UserDetailsService userDetailsService;

    PasswordEncoder passwordEncoder;


    String email = "harryjung0330@gmail.com";
    Role role;
    String password;

    Optional<AppUser> appUser;

    UserDetails userDetails;

    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

    private void initializeTestData(){
        email = "harryjung0330@gmail.com";

        password = "hello";
        role = Role.USER;
        Long id = Long.valueOf(1);

        appUser = Optional.of(
                AppUser.builder()
                        .email(email)
                        .role(role)
                        .password(password)
                        .id(id)
                        .build()
        );

        userDetails = new AppUserDetails(appUser.get());

        usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

    }

    private void initializeMockInstances(){
        userDetailsService = Mockito.mock(UserDetailsService.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        Mockito.when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        Mockito.when(passwordEncoder.matches(password, password)).thenReturn(true);

    }

    @BeforeEach
    public void beforeTest(){
        initializeTestData();
        initializeMockInstances();
        authenticationProvider = new EmailAuthenticationProvider(userDetailsService, passwordEncoder);
    }


    @Test
    public void testEmailAuthenticationProvider()
    {

        Authentication authentication = authenticationProvider.authenticate(usernamePasswordAuthenticationToken);

        assertEquals(authentication.getAuthorities(), List.of( new SimpleGrantedAuthority(role.toString())));
        assertEquals(authentication.getDetails(), userDetails);



    }

}
