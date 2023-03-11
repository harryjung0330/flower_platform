package com.example.flowerplatform.util.tokenManager.dto;

import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class VerifyTokenResultDto {

    private final long userId;

    private final Role role;

    private static final VerifyTokenResultDto invalidInstance = new VerifyTokenResultDto(-1 , null);

    public static VerifyTokenResultDto getInvalidInstance()
    {
        return invalidInstance;
    }

    public boolean isValid()
    {

        return !(this == getInvalidInstance());
    }
}
