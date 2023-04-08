package com.example.flowerplatform.service.dto.input;

import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.*;

@Builder
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Setter
@Getter
public class SaveExternalUserServiceDto
{
    private final Role role;

    private final AuthenticationProvider authenticationProvider;

    private final String registrationId;
}

