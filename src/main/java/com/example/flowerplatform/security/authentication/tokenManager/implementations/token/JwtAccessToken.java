package com.example.flowerplatform.security.authentication.tokenManager.implementations.token;

import com.example.flowerplatform.security.authentication.tokenManager.Token;
import lombok.*;

import java.util.Date;

@ToString
@EqualsAndHashCode
@Getter
public class JwtAccessToken extends Token
{
    private final String role;

    private final Long userId;


    @Builder
    public JwtAccessToken(String subject, Date expiresAt, Date createdAt, String role, Long userId)
    {
        super(subject, createdAt, expiresAt);
        this.role = role;
        this.userId = userId;
    }

}
