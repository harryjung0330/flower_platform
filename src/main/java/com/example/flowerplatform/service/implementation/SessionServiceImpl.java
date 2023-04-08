package com.example.flowerplatform.service.implementation;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.security.oauth2.dto.TokenDto;
import com.example.flowerplatform.service.SessionService;
import com.example.flowerplatform.service.dto.output.RefreshTokenServiceDto;
import com.example.flowerplatform.service.exceptions.UnusableSessionException;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtAccessToken;
import com.example.flowerplatform.util.tokenManager.implementations.token.JwtRefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    private final TokenManager tokenManager;


    @Override
    public RefreshTokenServiceDto refreshTokens(String refreshToken, Long userId) throws JWTVerificationException,
            UnusableSessionException {

        JwtRefreshToken postVerify = tokenManager.verifyToken(refreshToken, JwtRefreshToken.class);

        Optional<Session> session = sessionRepository.findById(postVerify.getSessionId());

        if(session.isEmpty() || !session.get().getRefreshToken().equals(refreshToken))
        {
            throw new UnusableSessionException("refresh token cannot be used since it was rotated");
        }

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .userId(userId)
                .sessionId(postVerify.getSessionId())
                .subject(postVerify.getSubject())
                .role(postVerify.getRole())
                .build();


        JwtAccessToken jwtAccessToken = JwtAccessToken.builder()
                .subject(postVerify.getSubject())
                .userId(postVerify.getUserId())
                .role(postVerify.getRole())
                .build();

        String newAccessToken = tokenManager.createToken(jwtAccessToken);
        String newRefreshToken = tokenManager.createToken(jwtRefreshToken);

        return RefreshTokenServiceDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(postVerify.getUserId())
                .build();
    }
}
