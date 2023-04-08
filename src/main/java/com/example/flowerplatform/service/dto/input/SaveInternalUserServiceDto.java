package com.example.flowerplatform.service.dto.input;

import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.*;

@Builder
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Setter
@Getter
public class SaveInternalUserServiceDto
{
    private final Role role;

    private final String email;

    private final String rawPassword;
}
