package com.example.flowerplatform.security.authentication.tokenManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public abstract class Token
{
    private final String subject;

    private final Date createdAt;

    private Date expiresAt;

}
