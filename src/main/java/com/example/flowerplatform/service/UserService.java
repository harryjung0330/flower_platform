package com.example.flowerplatform.service;

import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.service.dto.SaveExternalUserServiceDto;

import java.util.Optional;

public interface UserService
{
    public AppUser saveExternalUser(SaveExternalUserServiceDto saveExternalUserServiceDto);

    public Optional<AppUser> findByAuthenticationProviderAndRegistrationId(AuthenticationProvider authenticationProvider, String registrationId);
}
