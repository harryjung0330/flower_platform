package com.example.flowerplatform.security.authentication.dto;

import com.example.flowerplatform.dto.MessageFormat;
import lombok.*;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
public class TokenDto
{
    private final String access_token;

    private final String refresh_token;

    private final Long user_id;

}
