package com.example.flowerplatform.repository;

import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<AppUser, Long>
{
    public Optional<AppUser> findByEmail(String email);

    public Optional<AppUser> findByRegistrationIdAndAuthenticationProvider(String registrationId, AuthenticationProvider authenticationProvider);
}
