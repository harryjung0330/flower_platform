package com.example.flowerplatform.util.tokenManager.implementations.utils;

import com.example.flowerplatform.util.tokenManager.exception.JwtNotFoundException;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
import org.springframework.util.StringUtils;

public class TokenUtils {

    private static String extract(String jwtTokenHeader) {
        if(!StringUtils.hasText(jwtTokenHeader)){
            throw new JwtNotFoundException("Authorization header cannot be blank!");
        }

        if(jwtTokenHeader.length() < TokenProperties.HEADER_PREFIX.length()){
            throw new JwtNotFoundException("Invalid authorization header size");
        }

        if(!jwtTokenHeader.startsWith(TokenProperties.HEADER_PREFIX)){
            throw new JwtNotFoundException("Invalid token format");
        }

        return jwtTokenHeader.substring(TokenProperties.HEADER_PREFIX.length());
    }

}
