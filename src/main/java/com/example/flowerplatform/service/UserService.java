package com.example.flowerplatform.service;

import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.service.dto.input.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.dto.input.SaveInternalUserServiceDto;

import java.util.Optional;

public interface UserService
{
    public AppUser saveExternalUser(SaveExternalUserServiceDto saveExternalUserServiceDto);

    public AppUser saveInternalUser(SaveInternalUserServiceDto saveInternalUserServiceDto);

    public Optional<AppUser> findByAuthenticationProviderAndRegistrationId(AuthenticationProvider authenticationProvider, String registrationId);
}
