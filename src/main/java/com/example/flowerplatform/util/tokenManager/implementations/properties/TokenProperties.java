package com.example.flowerplatform.util.tokenManager.implementations.properties;

public interface TokenProperties {
    String HEADER_PREFIX = "Bearer ";
    String HEADER_ACCESS_KEY = "Authorization";
    String HEADER_REFRESH_KEY = "REFRESH_TOKEN";


    Long ACCESS_TOKEN_EXPIRED_TIME = 60000L * 5;
    Long REFRESH_TOKEN_EXPIRED_TIME = 60000L * 60 * 24 * 31;

    int ACCESS_TOKEN_DURATION_MIN = 5;
    int REFRESH_TOKEN_DURATION_MIN = 60 * 24 * 30;


    Long SIGNUP_SEND_REQUEST_TOKEN_EXPIRED_TIME = 60000L * 5;
    Long SIGNUP_CHECK_CODE_TOKEN_EXPIRED_TIME = 60000L * 100;

    Long PASSWORD_SEND_REQUEST_TOKEN_EXPIRED_TIME = 60000L * 5;
    Long PASSWORD_CHECK_CODE_TOKEN_EXPIRED_TIME = 60000L * 100;

    String ROLE = "role";

    String CODE = "code";
    String IS_VERIFIED = "is_verified";
    String PURPOSE_CODE = "purpose_code";
    String EMAIL = "email";




}
