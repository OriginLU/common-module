package com.zeroone.authoriztion.resource.provider.transfer;

import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * token获取器
 */
public class AuthorizedBearerTokenExtractor extends BearerTokenExtractor {

    private static final RequestMatcher GENERATE_CAPITAL_MATCHER = new AntPathRequestMatcher("/captcha/image-char-captcha/generate", "GET");
    private static final RequestMatcher LOGIN_MATCHER = new AntPathRequestMatcher("/auth/login", "POST");

    @Override
    protected String extractHeaderToken(HttpServletRequest request) {
        if (matchNotAuthUri(request)) {
            return null;
        }
        return super.extractHeaderToken(request);
    }

    /**
     * 匹配不需要权限url
     * @param request
     * @return
     */
    private boolean matchNotAuthUri(HttpServletRequest request) {
        return GENERATE_CAPITAL_MATCHER.matches(request) ||
                LOGIN_MATCHER.matches(request);
    }


}