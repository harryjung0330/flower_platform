package com.example.flowerplatform.security.oauth2;

import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.service.UserService;
import com.example.flowerplatform.service.dto.SaveExternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizedClientService implements OAuth2AuthorizedClientService
{
    private final UserService userService;


    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        log.debug("saveAuthorizedClient called!");

        log.debug("Authentication: " + principal.toString());
        log.debug("principalName: " + authorizedClient.getPrincipalName());
        log.debug("principal.getName(): " + principal.getName());//이걸 세이브
        log.debug("principal.getCredentials()" + String.valueOf(principal.getCredentials()));
        log.debug("principal.getDetails()" + String.valueOf(principal.getDetails()));
        log.debug("authorizedClient.getClientRegistration: " + authorizedClient.getClientRegistration());

        String principalName = authorizedClient.getPrincipalName();
        String provider = authorizedClient.getClientRegistration().getRegistrationId().toUpperCase();

        SaveExternalUserServiceDto saveExternalUserServiceDto = SaveExternalUserServiceDto.builder()
                .role(Role.REGISTRATION_NOT_COMPLETE_USER)
                .registrationId(principalName)
                .authenticationProvider(AuthenticationProvider.valueOf(provider))
                .build();

        try {
            userService.saveExternalUser(saveExternalUserServiceDto);
        }
        catch (DuplicateUserException duplicateUserException)
        {
            log.debug(duplicateUserException.getMessage());
        }
    }

    //have to implement this class when needed in the future
    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

    }
}
