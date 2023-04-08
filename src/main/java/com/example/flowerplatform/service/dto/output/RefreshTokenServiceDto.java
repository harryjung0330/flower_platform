package com.example.flowerplatform.service.dto.output;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@NoArgsConstructor(force = true)
@Data
@RequiredArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class RefreshTokenServiceDto {

    private final String accessToken;

    private final String refreshToken;

    private final Long userId;

    @Builder
    public RefreshTokenServiceDto(Long userId, String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
