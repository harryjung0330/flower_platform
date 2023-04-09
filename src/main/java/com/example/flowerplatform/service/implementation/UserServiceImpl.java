package com.example.flowerplatform.service.implementation;

import com.example.flowerplatform.exceptions.OAuth2SaveExternalUserException;
import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.UserService;
import com.example.flowerplatform.service.dto.input.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.dto.input.SaveInternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AppUser saveExternalUser(SaveExternalUserServiceDto saveExternalUserServiceDto)
    {
        try {
            AppUser appUser = AppUser.builder()
                    .role(saveExternalUserServiceDto.getRole())
                    .registrationId(saveExternalUserServiceDto.getRegistrationId())
                    .authenticationProvider(saveExternalUserServiceDto.getAuthenticationProvider())
                    .build();

            if (saveExternalUserServiceDto.getAuthenticationProvider() == AuthenticationProvider.INTERNAL)
                throw new OAuth2SaveExternalUserException("external user cannot have AuthenticationProvider INTERNAL!");


            return userRepository.save(appUser);
        }
        catch(Exception ex)
        {
            log.debug("type of exception is " + ex.getClass());
            if(ex instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException sqlException = (DataIntegrityViolationException) ex;

                log.debug("DataIntegrityViolationException message: " + sqlException.getMessage());

                throw new DuplicateUserException(sqlException.getMessage());

            }
            else{
                log.error("unexpected error occurred!");
                log.error(ex.toString());

                throw ex;
            }

        }

    }

    @Transactional
    @Override
    public Optional<AppUser> findByAuthenticationProviderAndRegistrationId(AuthenticationProvider authenticationProvider, String registrationId) {

        return userRepository.findByRegistrationIdAndAuthenticationProvider(registrationId, authenticationProvider);
    }

    @Transactional
    @Override
    public AppUser saveInternalUser(SaveInternalUserServiceDto saveInternalUserServiceDto)
    {
        try {
            AppUser appUser = AppUser.builder()
                    .role(saveInternalUserServiceDto.getRole())
                    .email(saveInternalUserServiceDto.getEmail())
                    .password(passwordEncoder.encode(saveInternalUserServiceDto.getRawPassword()))
                    .authenticationProvider(AuthenticationProvider.INTERNAL)
                    .build();

            return userRepository.save(appUser);
        }
        catch(Exception ex)
        {
            log.debug("type of exception is " + ex.getClass());
            if(ex instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException sqlException = (DataIntegrityViolationException) ex;

                log.debug("DataIntegrityViolationException message: " + sqlException.getMessage());

                throw new DuplicateUserException(sqlException.getMessage());

            }
            else{
                log.error("unexpected error occurred!");
                log.error(ex.toString());

                throw ex;
            }

        }
    }

    @PostConstruct
    public void createExampleUser(){
        SaveInternalUserServiceDto s1 = SaveInternalUserServiceDto.builder()
                .email("example1@gmail.com")
                .rawPassword("helloWorld!")
                .role(Role.USER)
                .build();

        SaveInternalUserServiceDto s2 = SaveInternalUserServiceDto.builder()
                .email("example2@gmail.com")
                .rawPassword("helloWorld!")
                .role(Role.USER)
                .build();

        try {
            saveInternalUser(s1);
            saveInternalUser(s2);
        }
        catch (Exception e) {
            log.error("failed to save user after initializing UserServiceImpl");
            log.error(e.getMessage());
        }
    }


}
