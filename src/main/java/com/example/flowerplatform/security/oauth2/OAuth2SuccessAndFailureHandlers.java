package com.example.flowerplatform.security.oauth2;

import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtRefreshToken;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.oauth2.dto.TokenDto;
import com.example.flowerplatform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessAndFailureHandlers {


    private final TokenManager tokenManager;

    private final ObjectMapper objectMapper;
    private final UserService userService;

    //인증 성공시 response를 어떻게 바꿀지
    @SneakyThrows
    public void oauthSuccessResponse(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        log.debug("oauthSuccessResponse called!");
        response.setStatus(HttpStatus.OK.value());

        if(!(authentication instanceof OAuth2AuthenticationToken)){
            throw new OAuth2AuthenticationException("the authentication token is not OAuth2AuthenticationToken type!");
        }

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        String principalName = oAuth2AuthenticationToken.getName();
        AuthenticationProvider provider = Enum.valueOf(AuthenticationProvider.class,
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().toUpperCase());

        AppUser appUser = userService.findByAuthenticationProviderAndRegistrationId(provider, principalName).get();

        if(appUser == null)
            throw new OAuth2AuthenticationException("unsaved user is authenticated!");

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(createAccessToken(appUser.getId(), appUser.getRole()))
                .refreshToken(createRefreshToken(appUser.getId(), appUser.getRole()))
                .userId(appUser.getId())
                .build();

        MessageFormat<TokenDto> tokenMessage = MessageFormat
                .<TokenDto>builder()
                .message("successfully authenticated through OAuth2")
                .data(tokenDto)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .build();

        String jsonStr = objectMapper.writeValueAsString(tokenMessage);

        log.debug("response is: " + jsonStr);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(jsonStr);
        response.getWriter().flush();
        response.getWriter().close();

    }

    //인증 실패시 response를 어떻게 바꿀지
    @SneakyThrows
    public void oauthFailureResponse(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException){

        log.debug("oauthFailureResponse is called!");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        log.error(authenticationException.toString());
        log.error(authenticationException.getMessage());
        log.error(authenticationException.getLocalizedMessage());

        MessageFormat tokenMessage = MessageFormat
                .builder()
                .message("failed to authenticate user: " + authenticationException.getMessage())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonStr = objectMapper.writeValueAsString(tokenMessage);
        log.debug("error message: " + jsonStr);

        response.getWriter().write(jsonStr);
        response.getWriter().flush();
        response.getWriter().close();
    }

    private String createAccessToken(Long userId, Role role){
        Date createdAt = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, TokenProperties.ACCESS_TOKEN_DURATION_MIN);

        Date expiresAt = calendar.getTime();

        JwtAccessToken jwtAccessToken = JwtAccessToken.builder()
                .createdAt(createdAt)
                .userId(userId)
                .role(role.name())
                .subject(String.valueOf(userId))
                .expiresAt(expiresAt)
                .build();

        return tokenManager.createToken(jwtAccessToken);
    }

    private String createRefreshToken(Long userId, Role role){
        Date createdAt = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, TokenProperties.REFRESH_TOKEN_DURATION_MIN);

        Date expiresAt = calendar.getTime();

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .createdAt(createdAt)
                .userId(userId)
                .role(role.name())
                .subject(String.valueOf(userId))
                .expiresAt(expiresAt)
                .build();

        return tokenManager.createToken(jwtRefreshToken);
    }
}
