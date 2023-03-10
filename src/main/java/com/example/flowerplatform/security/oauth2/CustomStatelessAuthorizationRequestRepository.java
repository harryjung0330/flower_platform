package com.example.flowerplatform.security.oauth2;


import com.example.flowerplatform.security.oauth2.util.CookieUtil;
import com.example.flowerplatform.security.oauth2.util.ObjectSerializationUtil;
import com.example.flowerplatform.security.oauth2.util.StringEncAndDecryptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomStatelessAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest>
{
    private final StringEncAndDecryptor stringEncAndDecryptor;

    private final ObjectSerializationUtil serializationUtil;



    //AuthorizationRequest를 Session을 통해서 저장하지 않고, request의 header에 저장하기 위하여!
    //AuthorizaionRequest에 state 값을 저장하여 csrf 공격을 방어한다.
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        try {
            return getAuthRequestFromRequest(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @SneakyThrows
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {

        log.debug("saveAuthorizeRequest called!");
        log.debug(authorizationRequest.toString());

        byte[] serialized = serializationUtil.serializeObject(authorizationRequest); //objectMapper.writeValueAsBytes(authorizationRequest);


        String encryptedRequest = stringEncAndDecryptor.encrypt(serialized);

        log.debug("encrypted request: " + encryptedRequest);

        String cookie = CookieUtil.generateCookie(encryptedRequest, request);

        log.debug("cookie to set: " + cookie);

        response.setHeader(HttpHeaders.SET_COOKIE, cookie);


    }

    @Override
    @SneakyThrows
    //인증후에 유저가 서버로 redirect 될때, 이 함수를 써서 OAuth2AuthorizationRequest를 생성함!
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return getAuthRequestFromRequest(request);
    }


    OAuth2AuthorizationRequest getAuthRequestFromRequest(HttpServletRequest request) throws Exception
    {
        log.debug("---------------getAuthRequestFromRequest called!");

        String encryptedRequest = CookieUtil.retrieve(request.getCookies()).orElseThrow(
                () -> new Exception("cookies! not found!"));

        log.debug("---------------encrypted cookie: " + encryptedRequest);

        byte[] decrypted = stringEncAndDecryptor.decrypt(encryptedRequest);

        log.debug("---------------decrypted cookie: " + decrypted.toString());

        OAuth2AuthorizationRequest authRequest = (OAuth2AuthorizationRequest) serializationUtil.deserializeObject(decrypted);//objectMapper.readValue(decrypted, OAuth2AuthorizationRequest.class);

        log.debug("--------------auth request: " + authRequest.toString());

        return authRequest;
    }
}
