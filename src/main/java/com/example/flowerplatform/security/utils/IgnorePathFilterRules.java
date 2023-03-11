package com.example.flowerplatform.security.utils;

import com.example.flowerplatform.security.authorization.JwtAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//어떤 filter에서 어떤 path가 ignore되어야 하는지 관리할수 있는 클래스
//미래에 더 많은 필터가 생길수도 있으므로, 각 필터마다 다른 리스트를 주었습니다.
@Component
@Slf4j
public class IgnorePathFilterRules
{
    List<String> authenticationIgnorePaths = List.of(
            "/spring/health-check", "/token/re-issue", "/user/login", "/oauth2/authorization/**", "/login/oauth2/code/**");

    List<String> authorizationIgnorePaths = List.of(
            "/spring/health-check", "/token/re-issue", "/user/login", "/oauth2/authorization/**", "/login/oauth2/code/**");

    Map< Class<? extends Filter>, List<AntPathRequestMatcher>> ignoreMap;

    public IgnorePathFilterRules(){
        ignoreMap = new HashMap<>();

        ignoreMap.put( AuthenticationFilter.class,
                authenticationIgnorePaths
                        .stream()
                        .map(path -> new AntPathRequestMatcher(path))
                        .collect(Collectors.toList())
        );

        ignoreMap.put( JwtAuthorizationFilter.class, authorizationIgnorePaths
                .stream()
                .map(path -> new AntPathRequestMatcher(path))
                .collect(Collectors.toList()));
    }

    public boolean shouldNotFilter(Class<? extends Filter> filterClass, HttpServletRequest request) {

        List<AntPathRequestMatcher> ignorePaths = ignoreMap.get(filterClass);


        if(ignorePaths != null)
        {
            for(AntPathRequestMatcher ignorePath: ignorePaths)
            {
                if(ignorePath.matches(request))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
