package com.example.flowerplatform.security.authentication.authenticationProvider;

import com.example.flowerplatform.security.authentication.exceptions.AuthenticationMissingInfoException;
import com.example.flowerplatform.security.authentication.exceptions.PasswordNotMatchingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        if(authentication == null)
        {
            throw new AuthenticationMissingInfoException("authentication object is null");
        }

        if(!supports(authentication.getClass()))
        {
            throw new UnsupportedOperationException("authentication is not UsernamePasswordAuthenticationToken type");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());

        if(userDetails == null)
        {
            throw new UnsupportedOperationException("unable to find user " + authentication.getPrincipal().toString());
        }
        String rawPassword = authentication.getCredentials().toString();
        String encodedPassword = userDetails.getPassword();

        if(rawPassword == null || encodedPassword == null)
        {
            throw  new AuthenticationMissingInfoException("Either raw password or encoded password is missing!");

        }

        if(passwordEncoder.matches(rawPassword,encodedPassword))
        {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken.setDetails(userDetails);

            return usernamePasswordAuthenticationToken;
        }

        throw new PasswordNotMatchingException("email " + userDetails.getUsername() + " failed authentication due to password mismatch");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
