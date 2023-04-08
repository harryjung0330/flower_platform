package com.example.flowerplatform.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Setter
@Getter
@ToString
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenRefreshDto {

    @NotBlank(message = "refresh token is required to renew your tokens")
    private final String refreshToken;

    @NotBlank(message = "user id is reuqired to renew your tokens")
    private final Long userId;
}

