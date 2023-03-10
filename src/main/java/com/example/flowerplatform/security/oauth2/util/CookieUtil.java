package com.example.flowerplatform.security.oauth2.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;

import jakarta.servlet.http.Cookie;
import java.util.Optional;

import static java.util.Objects.isNull;

// 특정한 값을 사용하여 쿠키를 만들거나, 쿠키에서 원하는 값을 parse하는 클래스
public class CookieUtil
{
    private static final String COOKIE_NAME = "SESSION_ID";

    private static final String COOKIE_DOMAIN = "localhost";
    private static final Boolean HTTP_ONLY = Boolean.TRUE;
    private static final Boolean SECURE = Boolean.FALSE;

    public static Optional<String> retrieve(Cookie[] cookies) {
        if (isNull(cookies)) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(COOKIE_NAME)) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public static String generateCookie(@NonNull String value, HttpServletRequest request) {
        // Build cookie instance
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        if (!"localhost".equals(COOKIE_DOMAIN)) { // https://stackoverflow.com/a/1188145
            cookie.setDomain(COOKIE_DOMAIN);
        }
        cookie.setHttpOnly(HTTP_ONLY);
        cookie.setSecure(SECURE);
        cookie.setMaxAge((int) 5000);
        cookie.setPath("/");

        // Generate cookie string
        Rfc6265CookieProcessor processor = new Rfc6265CookieProcessor();

        return processor.generateHeader(cookie, request );
    }

}
