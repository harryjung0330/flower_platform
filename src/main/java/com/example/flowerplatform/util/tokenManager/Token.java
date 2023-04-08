package com.example.flowerplatform.util.tokenManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
@Setter
public abstract class Token
{
    private final String subject;

    private Date createdAt;

    private Date expiresAt;

}
