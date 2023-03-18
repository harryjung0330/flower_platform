package com.example.flowerplatform.integrationTest;

import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtRefreshToken;
import com.example.flowerplatform.util.tokenManager.implementations.tokenManager.TokenManagerImpl;
import com.example.flowerplatform.util.tokenManager.implementations.tokenWorkers.JwtAccessTokenWorker;
import com.example.flowerplatform.util.tokenManager.implementations.tokenWorkers.JwtRefreshTokenWorker;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {JwtRefreshTokenWorker.class, JwtAccessTokenWorker.class, TokenManagerImpl.class})
@Slf4j
public class TokenManagerImplTest
{
    @MockBean
    SessionRepository sessionRepository;

    @Autowired
    TokenManagerImpl tokenManagerImpl;

    @Test
    public void createAccessToken(){
        //given
        Long userId = 1L;
        Role role = Role.USER;
        Date createdAt = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(createdAt);
        c.add(Calendar.MINUTE, 5);

        Date expiresAt = c.getTime();

        JwtAccessToken jwtAccessToken = JwtAccessToken.builder()
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .subject(String.valueOf(1L))
                .role(role.name())
                .userId(userId)
                .build();

        //when
        String res = tokenManagerImpl.createToken(jwtAccessToken);
        log.debug("access token: " + res);

        //then
        assertNotNull(res);

    }


    @Test
    public void verifyAccessToken(){
        //given
        Long userId = 1L;
        Role role = Role.USER;
        Date createdAt = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(createdAt);
        c.add(Calendar.MINUTE, 5);

        Date expiresAt = c.getTime();

        JwtAccessToken jwtAccessToken = JwtAccessToken.builder()
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .subject(String.valueOf(1L))
                .role(role.name())
                .userId(userId)
                .build();


        String res = tokenManagerImpl.createToken(jwtAccessToken);
        log.debug("access token: " + res);

        //when
        JwtAccessToken verifiedAccessToken = tokenManagerImpl.verifyToken(res, JwtAccessToken.class);

        //then
        assertEquals(verifiedAccessToken.getSubject(), jwtAccessToken.getSubject());
        assertEquals(verifiedAccessToken.getCreatedAt().getTime() / 1000, jwtAccessToken.getCreatedAt().getTime() / 1000);
        assertEquals(verifiedAccessToken.getExpiresAt().getTime() / 1000, jwtAccessToken.getExpiresAt().getTime() / 1000);
        assertEquals(verifiedAccessToken.getRole(), jwtAccessToken.getRole());
        assertEquals(verifiedAccessToken.getUserId(), jwtAccessToken.getUserId());

    }

    @Test
    public void createRefreshToken(){
        //given
        Long userId = 1L;
        Role role = Role.USER;
        Date createdAt = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(createdAt);
        c.add(Calendar.MINUTE, 5);

        Date expiresAt = c.getTime();

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .subject(String.valueOf(1L))
                .role(role.name())
                .userId(userId)
                .build();

        Session postSaveSession = Session.builder()
                .sessionId(1L)
                .expiresAt(jwtRefreshToken.getExpiresAt())
                .userId(jwtRefreshToken.getUserId())
                .build();

        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSaveSession);

        //when
        String res = tokenManagerImpl.createToken(jwtRefreshToken);
        log.debug("refresh token: " + res);

        //then
        assertNotNull(res);
        verify(sessionRepository, times(2)).save(any(Session.class));

    }


    @Test
    public void verifyRefreshToken(){
        //given
        Long userId = 1L;
        Role role = Role.USER;
        Date createdAt = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(createdAt);
        c.add(Calendar.MINUTE, 5);

        Date expiresAt = c.getTime();

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .subject(String.valueOf(1L))
                .role(role.name())
                .userId(userId)
                .build();

        Session postSaveSession = Session.builder()
                .sessionId(1L)
                .expiresAt(jwtRefreshToken.getExpiresAt())
                .userId(jwtRefreshToken.getUserId())
                .build();

        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSaveSession);

        //when
        String res = tokenManagerImpl.createToken(jwtRefreshToken);
        //when
        JwtRefreshToken verifiedRefreshToken = tokenManagerImpl.verifyToken(res, JwtRefreshToken.class);

        //then
        assertEquals(verifiedRefreshToken.getSubject(), jwtRefreshToken.getSubject());
        assertEquals(verifiedRefreshToken.getCreatedAt().getTime() / 1000, jwtRefreshToken.getCreatedAt().getTime() / 1000);
        assertEquals(verifiedRefreshToken.getExpiresAt().getTime() / 1000, jwtRefreshToken.getExpiresAt().getTime() / 1000);
        assertEquals(verifiedRefreshToken.getRole(), jwtRefreshToken.getRole());
        assertEquals(verifiedRefreshToken.getUserId(), jwtRefreshToken.getUserId());

    }





}
