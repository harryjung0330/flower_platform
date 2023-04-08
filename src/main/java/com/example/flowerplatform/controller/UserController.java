package com.example.flowerplatform.controller;

import com.example.flowerplatform.controller.dto.request.TokenRefreshDto;
import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.service.SessionService;
import com.example.flowerplatform.service.dto.output.RefreshTokenServiceDto;
import com.example.flowerplatform.util.tokenManager.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController
{
    private final TokenManager tokenManager;

    private final SessionService sessionService;

    @PostMapping("{tokens}")
    public ResponseEntity<MessageFormat> refreshToken(@RequestBody TokenRefreshDto tokenRefreshDto) {
        RefreshTokenServiceDto result =
                sessionService.refreshTokens(tokenRefreshDto.getRefreshToken(), tokenRefreshDto.getUserId());

        MessageFormat<RefreshTokenServiceDto> msg = MessageFormat.<RefreshTokenServiceDto>builder()
                .message("successfully refreshed token")
                .status(HttpStatus.OK.value())
                .data(result)
                .timestamp(new Date())
                .build();

        return ResponseEntity.ok(msg);

    }


}
