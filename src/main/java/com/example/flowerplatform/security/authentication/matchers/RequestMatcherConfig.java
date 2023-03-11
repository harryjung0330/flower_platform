package com.example.flowerplatform.security.authentication.matchers;

import com.example.flowerplatform.security.authentication.filter.AuthenticationFilter;
import com.example.flowerplatform.security.securityConfig.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class RequestMatcherConfig
{
    @Bean(name = AuthenticationFilter.MATCHER_NAME)
    public RequestMatcher logInRequestMatcher(){
        return  new MvcRequestMatcher(new HandlerMappingIntrospector(),
                SecurityConfig.LOGIN_PATH);
    }

}
