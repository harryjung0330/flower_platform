package com.example.flowerplatform.unitTest;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.SessionService;
import com.example.flowerplatform.service.dto.output.RefreshTokenServiceDto;
import com.example.flowerplatform.service.exceptions.UnusableSessionException;
import com.example.flowerplatform.service.implementation.SessionServiceImpl;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtRefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {SessionServiceImpl.class})
@Slf4j
public class SessionServiceImplTest {

    @Autowired
    SessionService sessionService;
    @MockBean
    TokenManager tokenManager;

    @MockBean
    SessionRepository sessionRepository;

    @Test
    public void testRefreshTokenSuccess(){
        //given
        String refreshToken = "aaaaaaaaa";
        Long sessionId = 1L;
        Long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 1000);


        JwtRefreshToken refreshTokenAfterVerify = JwtRefreshToken.builder()
                .sessionId(sessionId)
                .subject(String.valueOf(sessionId))
                .createdAt(createdAt)
                .expiresAt(calendar.getTime())
                .userId(userId)
                .role(Role.USER.name())
                .build();

        Session sessionAfterFind = Session.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiresAt(calendar.getTime())
                .sessionId(sessionId)
                .build();

        String newAccessToken = "aaaaaaaa";
        String newRefreshToken = "aaaaaaaaa";

        when(tokenManager.verifyToken(refreshToken, JwtRefreshToken.class)).thenReturn(refreshTokenAfterVerify);
        when(sessionRepository.findById(any(Long.class))).thenReturn(Optional.of(sessionAfterFind));
        when(tokenManager.createToken(any(JwtRefreshToken.class))).thenReturn(newRefreshToken);
        when(tokenManager.createToken(any(JwtAccessToken.class))).thenReturn(newAccessToken);

        //when
        RefreshTokenServiceDto r = sessionService.refreshTokens(refreshToken, userId);

        //then
        assertEquals(r.getRefreshToken(), newRefreshToken);
        assertEquals(r.getAccessToken(), newAccessToken);
        verify(sessionRepository, atLeast(1)).findById(sessionId);
    }

    @Test
    public void testRefreshTokenSessionNotInDB(){
        //given
        String refreshToken = "aaaaaaaaa";
        Long sessionId = 1L;
        Long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 1000);


        JwtRefreshToken refreshTokenAfterVerify = JwtRefreshToken.builder()
                .sessionId(sessionId)
                .subject(String.valueOf(sessionId))
                .createdAt(createdAt)
                .expiresAt(calendar.getTime())
                .userId(userId)
                .role(Role.USER.name())
                .build();

        Session sessionAfterFind = Session.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiresAt(calendar.getTime())
                .sessionId(sessionId)
                .build();

        String newAccessToken = "aaaaaaaa";
        String newRefreshToken = "aaaaaaaaa";

        when(tokenManager.verifyToken(refreshToken, JwtRefreshToken.class)).thenReturn(refreshTokenAfterVerify);
        when(sessionRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        when(tokenManager.createToken(any(JwtRefreshToken.class))).thenReturn(newRefreshToken);
        when(tokenManager.createToken(any(JwtAccessToken.class))).thenReturn(newAccessToken);

        //when & then
        Assertions.assertThrows(UnusableSessionException.class, () -> {
            sessionService.refreshTokens(refreshToken, userId);
        });
    }

    @Test
    public void testRefreshTokenExpired(){
        //given
        String refreshToken = "aaaaaaaaa";
        Long sessionId = 1L;
        Long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 1000);


        JwtRefreshToken refreshTokenAfterVerify = JwtRefreshToken.builder()
                .sessionId(sessionId)
                .subject(String.valueOf(sessionId))
                .createdAt(createdAt)
                .expiresAt(calendar.getTime())
                .userId(userId)
                .role(Role.USER.name())
                .build();

        Session sessionAfterFind = Session.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiresAt(calendar.getTime())
                .sessionId(sessionId)
                .build();

        String newAccessToken = "aaaaaaaa";
        String newRefreshToken = "aaaaaaaaa";

        when(tokenManager.verifyToken(refreshToken, JwtRefreshToken.class)).thenThrow(TokenExpiredException.class);
        when(sessionRepository.findById(any(Long.class))).thenReturn(Optional.of(sessionAfterFind));;
        when(tokenManager.createToken(any(JwtRefreshToken.class))).thenReturn(newRefreshToken);
        when(tokenManager.createToken(any(JwtAccessToken.class))).thenReturn(newAccessToken);

        //when & then
        Assertions.assertThrows(TokenExpiredException.class, () -> {
            sessionService.refreshTokens(refreshToken, userId);
        });
    }





}
