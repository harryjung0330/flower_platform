package com.example.flowerplatform.unitTest;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.flowerplatform.security.authentication.exceptions.UnsupportedTokenTypeException;
import com.example.flowerplatform.security.authentication.tokenManager.Token;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.token.JwtRefreshToken;
import com.example.flowerplatform.security.authentication.tokenManager.implementations.tokenWorkers.JwtAccessTokenWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class JwtAccessTokenWorkerTest
{
    @Autowired
    JwtAccessTokenWorker jwtAccessTokenWorker;

    @Test
    public void testTwoSecretKeys(){
        Long userId = 1L;
        String role = "USER";
        long sessionId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        Token jwtToken = JwtAccessToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();
        String res = jwtAccessTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        //intentionally change primary key to test whether it uses secondary key
        jwtAccessTokenWorker.setPrimarySecretKey("none!");

        JwtAccessToken t = jwtAccessTokenWorker.verifyToken(res, JwtAccessToken.class);

        assertEquals(jwtToken, t);
        assertEquals(t.getClass(), JwtAccessToken.class);
    }

    @Test
    public void testCreateJwtAccessTokenWorker(){
        String role = "USER";
        Long userId = 1L;

        Calendar calendar = Calendar.getInstance();

        Date createdAt = new Date();

        calendar.setTime(createdAt);

        log.debug("created time: " + createdAt.toString());

        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        log.debug("expiring time: " + expiresAt.toString());

        Token jwtToken = JwtAccessToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();

        String res = jwtAccessTokenWorker.createToken(jwtToken);

        log.info("created token is following: \n" + res);

        assertNotNull(res);
    }

    @Test
    public void testVerifyJwtAccessToken(){
        Long userId = 1L;
        String role = "USER";
        long sessionId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        Token jwtToken = JwtAccessToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();
        String res = jwtAccessTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        JwtAccessToken t = jwtAccessTokenWorker.verifyToken(res, JwtAccessToken.class);

        assertEquals(jwtToken, t);
        assertEquals(t.getClass(), JwtAccessToken.class);
    }

    @Test(expected = UnsupportedTokenTypeException.class)
    public void testVerifyRefreshToken(){
        String role = "USER";
        long sessionId = 1L;
        Long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        Token jwtToken = JwtRefreshToken.builder()
                .role(role)
                .sessionId(sessionId)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();
        String res = jwtAccessTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        JwtRefreshToken t = jwtAccessTokenWorker.verifyToken(res, JwtRefreshToken.class);

        assertEquals(jwtToken, t);
    }

    @Test(expected = TokenExpiredException.class)
    public void testVerifyExpiredAccessToken(){
        String role = "USER";
        long sessionId = 1L;
        Long userId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date cur = new Date();
        calendar.setTime(cur);
        calendar.add(Calendar.MINUTE, -5);

        Date createdAt = calendar.getTime();

        calendar.add(Calendar.MINUTE,+2);

        Date expiresAt = calendar.getTime();

        Token jwtToken = JwtAccessToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();

        String res = jwtAccessTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        JwtAccessToken t = jwtAccessTokenWorker.verifyToken(res, JwtAccessToken.class);

        assertEquals(jwtToken, t);
    }

    @Test()
    public void testReadExpiredAccessToken(){
        String role = "USER";
        Long userId = 1L;
        long sessionId = 1L;
        Calendar calendar = Calendar.getInstance();
        Date cur = new Date();
        calendar.setTime(cur);
        calendar.add(Calendar.MINUTE, -5);

        Date createdAt = calendar.getTime();

        calendar.add(Calendar.MINUTE,+2);

        Date expiresAt = calendar.getTime();

        Token jwtToken = JwtAccessToken.builder()
                .role(role)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .userId(userId)
                .build();

        String res = jwtAccessTokenWorker.createToken(jwtToken);

        assertNotNull(res);

        JwtAccessToken t = jwtAccessTokenWorker.readToken(res, JwtAccessToken.class);

        assertEquals(jwtToken, t);
    }
}
