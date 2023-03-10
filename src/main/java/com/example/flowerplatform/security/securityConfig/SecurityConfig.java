package com.example.flowerplatform.security.securityConfig;

import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.security.authentication.handlers.OnFailureAuthenticationHandler;
import com.example.flowerplatform.security.authentication.handlers.OnSuccessAuthenticationHandler;
import com.example.flowerplatform.security.authentication.tokenManager.TokenManagerImplt;
import com.example.flowerplatform.security.filters.AuthenticationFilter;
import com.example.flowerplatform.security.filters.JwtAuthorizationExceptionFilter;
import com.example.flowerplatform.security.filters.JwtAuthorizationFilter;
import com.example.flowerplatform.security.oauth2.CustomAuthorizedClientService;
import com.example.flowerplatform.security.oauth2.CustomStatelessAuthorizationRequestRepository;
import com.example.flowerplatform.security.oauth2.OAuth2SuccessAndFailureHandlers;
import com.example.flowerplatform.security.utils.IgnorePathFilterRules;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] ANONYMOUS_AUTH_ENTRY_POINT = {
            "/spring/health-check", "/token/re-issue", "/user/login", "/user/register/**", "/user/id-inquiry/**", "/user/pw-inquiry/**", "/user/password/**"
    };

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final TokenManagerImplt tokenManager;

    private static final String LOGIN_PATH = "/user/login";

    private final AuthenticationManager authenticationManager;

    private final OnSuccessAuthenticationHandler onSuccessAuthenticationHandler;

    private final OnFailureAuthenticationHandler onFailureAuthenticationHandler;

    private final Validator validator;

    private final IgnorePathFilterRules ignorePathFilterRules;

    //-------------------------------- for oauth2 login ---------------------------------

    private final CustomAuthorizedClientService customAuthorizedClientService;

    private final CustomStatelessAuthorizationRequestRepository customStatelessAuthorizationRequestRepository;

    private final OAuth2SuccessAndFailureHandlers OAuth2SuccessAndFailureHandlers;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {

        http.formLogin().disable();
        http.httpBasic().disable();
        http.csrf().disable();



        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //addAuthenticationFilter(http);

        //addAuthorizationFilter(http);

        /*
        http.authorizeHttpRequests()
                .requestMatchers(ANONYMOUS_AUTH_ENTRY_POINT).permitAll()
                .anyRequest().hasAuthority(Role.USER.toString());


         */

        http.oauth2Login(config -> {


            config.authorizationEndpoint(subconfig -> {
                subconfig.authorizationRequestRepository(this.customStatelessAuthorizationRequestRepository);
            });



            config.authorizedClientService(customAuthorizedClientService);

            config.successHandler(OAuth2SuccessAndFailureHandlers::oauthSuccessResponse);
            config.failureHandler(OAuth2SuccessAndFailureHandlers::oauthFailureResponse);
        });;


        http.authorizeHttpRequests()
                .anyRequest().permitAll();




        return http.build();
    }


    private void addAuthenticationFilter(HttpSecurity http) throws Exception
    {
        //set authentication filter
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(
                        new MvcRequestMatcher(new HandlerMappingIntrospector(),
                                LOGIN_PATH),
                        authenticationManager,
                        validator,
                        objectMapper);

        authenticationFilter.setAuthenticationSuccessHandler(onSuccessAuthenticationHandler);
        authenticationFilter.setAuthenticationFailureHandler(onFailureAuthenticationHandler);

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void addAuthorizationFilter(HttpSecurity http)
    {
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
                authenticationManager,
                userRepository,
                tokenManager,
                ignorePathFilterRules);


        JwtAuthorizationExceptionFilter jwtAuthorizationExceptionFilter = new JwtAuthorizationExceptionFilter(
                objectMapper
        );

        http.addFilterAfter(jwtAuthorizationExceptionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(jwtAuthorizationFilter, JwtAuthorizationExceptionFilter.class);

    }





}