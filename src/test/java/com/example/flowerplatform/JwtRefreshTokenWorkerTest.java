package com.example.flowerplatform;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.security.authentication.exceptions.UnsupportedTokenTypeException;
import com.example.flowerplatform.security.authentication.tokenManager.Token;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.token.JwtRefreshToken;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.tokenWorkers.JwtAccessTokenWorker;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.tokenWorkers.JwtRefreshTokenWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JwtRefreshTokenWorker.class})
@Slf4j
public class JwtRefreshTokenWorkerTest
{
    @MockBean
    SessionRepository sessionRepository;

    @Autowired
    JwtRefreshTokenWorker jwtRefreshTokenWorker;

    @Test
    public void testCreateJwtRefreshTokenWorker(){

        //given
        String role = "USER";
        long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();
        calendar.setTime(createdAt);

        log.debug("created time: " + createdAt.toString());

        calendar.add(Calendar.MINUTE, 5);
        Date expiresAt = calendar.getTime();

        log.debug("expiring time: " + expiresAt.toString());

        Token jwtToken = JwtRefreshToken.builder()
                .role(role)

                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();

        Session postSave = Session.builder()
                        .userId(userId)
                                .sessionId(1L)
                                        .build();


        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSave);
        

        //when
        String res = jwtRefreshTokenWorker.createToken(jwtToken);
        log.info("created token is following: \n" + res);

        //then
        assertNotNull(res);
        verify(sessionRepository, atLeast(2)).save(any(Session.class));
    }

    @Test
    public void testVerifyJwtRefreshToken(){
        String role = "USER";
        long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        JwtRefreshToken jwtToken = JwtRefreshToken.builder()
                .subject(String.valueOf(userId))
                .role(role)
                .userId(userId)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();

        Session postSave = Session.builder()
                .userId(userId)
                .sessionId(1L)
                .build();
        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSave);

        String res = jwtRefreshTokenWorker.createToken(jwtToken);

        log.debug(res);

        assertNotNull(res);

        JwtRefreshToken t = jwtRefreshTokenWorker.verifyToken(res, JwtRefreshToken.class);

        //실제로 토큰에는 초단위로 저장을 함으로 같지 않다! 따라서 1000을 나눔!
        assertEquals(jwtToken.getCreatedAt().getTime() /1000 , t.getCreatedAt().getTime() / 1000);
        assertEquals(jwtToken.getExpiresAt().getTime() / 1000, t.getExpiresAt().getTime()/ 1000);
        assertEquals(jwtToken.getSubject(), t.getSubject());
        assertEquals(jwtToken.getRole(), t.getRole());
        assertEquals(jwtToken.getUserId(), t.getUserId());
        assertEquals(t.getClass(), JwtRefreshToken.class);


    }

    @Test(expected = UnsupportedTokenTypeException.class)
    public void testVerifyAccessToken(){
        String role = "USER";
        long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        JwtAccessToken jwtToken = JwtAccessToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();

        Session postSave = Session.builder()
                .userId(userId)
                .sessionId(1L)
                .build();
        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSave);

        String res = jwtRefreshTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        JwtAccessToken t = jwtRefreshTokenWorker.verifyToken(res, JwtAccessToken.class);

        //실제로 토큰에는 초단위로 저장을 함으로 같지 않다! 따라서 1000을 나눔!
        assertEquals(jwtToken.getCreatedAt().getTime() /1000 , t.getCreatedAt().getTime() / 1000);
        assertEquals(jwtToken.getExpiresAt().getTime() / 1000, t.getExpiresAt().getTime()/ 1000);
        assertEquals(jwtToken.getSubject(), t.getSubject());
        assertEquals(jwtToken.getRole(), t.getRole());
        assertEquals(jwtToken.getUserId(), t.getUserId());
        assertEquals(t.getClass(), JwtRefreshToken.class);
    }

    @Test(expected = TokenExpiredException.class)
    public void testVerifyExpiredAccessToken(){
        String role = "USER";
        long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date cur = new Date();
        calendar.setTime(cur);
        calendar.add(Calendar.MINUTE, -5);

        Date createdAt = calendar.getTime();

        calendar.add(Calendar.MINUTE,+2);

        Date expiresAt = calendar.getTime();

        JwtRefreshToken jwtToken = JwtRefreshToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();

        Session postSave = Session.builder()
                .userId(userId)
                .sessionId(1L)
                .build();

        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSave);


        String res = jwtRefreshTokenWorker.createToken(jwtToken);



        assertNotNull(res);

        JwtRefreshToken t = jwtRefreshTokenWorker.verifyToken(res, JwtRefreshToken.class);

        //실제로 토큰에는 초단위로 저장을 함으로 같지 않다! 따라서 1000을 나눔!
        assertEquals(jwtToken.getCreatedAt().getTime() /1000 , t.getCreatedAt().getTime() / 1000);
        assertEquals(jwtToken.getExpiresAt().getTime() / 1000, t.getExpiresAt().getTime()/ 1000);
        assertEquals(jwtToken.getSubject(), t.getSubject());
        assertEquals(jwtToken.getRole(), t.getRole());
        assertEquals(jwtToken.getUserId(), t.getUserId());
        assertEquals(t.getClass(), JwtRefreshToken.class);
    }

    @Test
    public void testReadExpiredAccessToken(){
        String role = "USER";
        long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date cur = new Date();
        calendar.setTime(cur);
        calendar.add(Calendar.MINUTE, -5);

        Date createdAt = calendar.getTime();

        calendar.add(Calendar.MINUTE,+2);

        Date expiresAt = calendar.getTime();

        JwtRefreshToken jwtToken = JwtRefreshToken.builder()
                .role(role)
                .userId(userId)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();

        Session postSave = Session.builder()
                .userId(userId)
                .sessionId(1L)
                .build();
        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(postSave);

        String res = jwtRefreshTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        JwtRefreshToken t = jwtRefreshTokenWorker.readToken(res, JwtRefreshToken.class);

        //실제로 토큰에는 초단위로 저장을 함으로 같지 않다! 따라서 1000을 나눔!
        assertEquals(jwtToken.getCreatedAt().getTime() /1000 , t.getCreatedAt().getTime() / 1000);
        assertEquals(jwtToken.getExpiresAt().getTime() / 1000, t.getExpiresAt().getTime()/ 1000);
        assertEquals(jwtToken.getSubject(), t.getSubject());
        assertEquals(jwtToken.getRole(), t.getRole());
        assertEquals(jwtToken.getUserId(), t.getUserId());
        assertEquals(t.getClass(), JwtRefreshToken.class);
    }
}
