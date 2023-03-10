package com.example.flowerplatform.security.authentication.tokenManager.implementations.token;

import com.example.flowerplatform.security.authentication.tokenManager.Token;
import lombok.*;

import java.util.Date;


@ToString
@EqualsAndHashCode
@Getter
public class JwtRefreshToken extends Token
{
    private final String role;

    private final Long sessionId;

    private final Long userId;

    @Builder
    public JwtRefreshToken(String subject, Date expiresAt, Date createdAt, String role, long sessionId, long userId)
    {
        super(subject, createdAt, expiresAt);
        this.role = role;
        this.sessionId = sessionId;
        this.userId = userId;
    }

}
