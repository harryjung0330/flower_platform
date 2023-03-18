package com.example.flowerplatform.security.authorization;

import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.security.utils.IgnorePathFilterRules;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
//@Component
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final TokenManager tokenManager;
    private final IgnorePathFilterRules ignorePathFilterUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  TokenManager tokenManager, IgnorePathFilterRules ignorePathFilterUtil) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
        this.ignorePathFilterUtil = ignorePathFilterUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        //ignorePathFilterUtil을 사용해서 필터링을 해야하는지 안해야하는지 정함.
        return ignorePathFilterUtil.shouldNotFilter(this.getClass(), request);
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("인증이나 권한이 필요한 요청: " + request.getRequestURI() );

        JwtAccessToken accessToken = tokenManager.verifyToken(request.getHeader(TokenProperties.HEADER_ACCESS_KEY), JwtAccessToken.class);


        //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(accessToken.getSubject(), null,
                List.of(
                        new SimpleGrantedAuthority(accessToken.getRole())
                )
        );

        //authentication detail에 accessToken을 주입한다.
        authentication.setDetails(accessToken);



        //강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);




        chain.doFilter(request, response);
    }
}

