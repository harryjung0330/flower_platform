package com.example.flowerplatform.util.tokenManager.implementations.token;

import com.example.flowerplatform.util.tokenManager.Token;
import lombok.*;

import java.util.Date;


@ToString
@EqualsAndHashCode
@Getter
@Setter
public class JwtRefreshToken extends Token
{
    private final String role;

    @Builder.Default
    private Long sessionId = null;

    private final Long userId;

    @Builder
    public JwtRefreshToken(String subject, Date expiresAt, Date createdAt, String role, Long sessionId, Long userId)
    {
        super(subject, createdAt, expiresAt);
        this.role = role;
        this.sessionId = sessionId;
        this.userId = userId;
    }

}
