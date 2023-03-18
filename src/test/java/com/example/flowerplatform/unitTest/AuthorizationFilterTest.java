package com.example.flowerplatform.unitTest;

import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.authorization.JwtAuthorizationFilter;
import com.example.flowerplatform.security.utils.IgnorePathFilterRules;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import java.util.Date;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {JwtAuthorizationFilter.class})
@Slf4j
public class AuthorizationFilterTest
{
    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    IgnorePathFilterRules ignorePathFilterRules;

    @MockBean
    TokenManager tokenManager;

    @MockBean
    AuthenticationManager authenticationManager;

    @Test
    @SneakyThrows
    public void doFilterInternalTest(){

        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        String token = "aasdfasdfa.asdfasdfa.asdfas";
        JwtAccessToken jwtAccessToken = JwtAccessToken.builder()
                .createdAt(new Date())
                .expiresAt(new Date())
                .role(Role.USER.name())
                .userId(1L)
                .subject(String.valueOf(1L))
                .build();

        when(request.getHeader(any(String.class))).thenReturn(token);
        when(ignorePathFilterRules.shouldNotFilter(JwtAuthorizationFilter.class, request)).thenReturn(false);
        when(tokenManager.verifyToken(token, JwtAccessToken.class)).thenReturn(jwtAccessToken);

        //when
        jwtAuthorizationFilter.doFilter(request, response, filterChain);
        //then

        verify(tokenManager, times(1)).verifyToken(token, JwtAccessToken.class);

    }
}

/*

    private final UserRepository userRepository;
    private final TokenManager tokenManager;
    private final IgnorePathFilterRules ignorePathFilterUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository,
                                  TokenManager tokenManager, IgnorePathFilterRules ignorePathFilterUtil) {
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


 */