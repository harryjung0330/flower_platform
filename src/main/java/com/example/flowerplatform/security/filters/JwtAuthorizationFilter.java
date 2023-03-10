package com.example.flowerplatform.security.filters;

import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.security.authentication.tokenManager.TokenManagerImplt;

import com.example.flowerplatform.security.authentication.tokenManager.dto.VerifyTokenResultDto;
import com.example.flowerplatform.security.utils.IgnorePathFilterRules;
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
import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final TokenManagerImplt tokenManager;
    private final IgnorePathFilterRules ignorePathFilterUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository,
                                  TokenManagerImplt tokenManager, IgnorePathFilterRules ignorePathFilterUtil) {
        super(authenticationManager);
        this.userRepository = userRepository;
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

        /*
        Authentication authentication = new UsernamePasswordAuthenticationToken("harry",
                null, List.of(new SimpleGrantedAuthority("USER")));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);


         */

        VerifyTokenResultDto verifyTokenResultDto = tokenManager.verifyToken(request.getHeader(TokenProperties.HEADER_ACCESS_KEY));

        if(verifyTokenResultDto != null){
            //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(verifyTokenResultDto, null,
                    List.of(
                        new SimpleGrantedAuthority(verifyTokenResultDto.getRole().toString())
                    )
            );

            //강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
        }



        chain.doFilter(request, response);
    }
}

