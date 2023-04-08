package com.example.flowerplatform.service;

import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.service.dto.output.RefreshTokenServiceDto;

public interface SessionService
{
    public RefreshTokenServiceDto refreshTokens(String refreshToken, Long userId);
}
