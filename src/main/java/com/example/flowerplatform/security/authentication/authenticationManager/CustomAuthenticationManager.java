package com.example.flowerplatform.security.authentication.authenticationManager;

import com.example.flowerplatform.security.authentication.exceptions.UnsupportedTokenTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {


    private final List<AuthenticationProvider> authenticationProviderList;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {



        for(AuthenticationProvider authenticationProvider: authenticationProviderList)
        {


            if(authenticationProvider.supports(authentication.getClass()))
            {
                return authenticationProvider.authenticate(authentication);
            }


        }

        throw new UnsupportedTokenTypeException("no provider can authenticate token " + authentication.getClass().toString() + " type");
    }

}
