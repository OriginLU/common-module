package com.zeroone.authoriztion.resource.filter;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.constant.SecurityConstants;
import com.zeroone.authoriztion.resource.service.OAuth2AuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  刷新Token过滤器
 */
public class RefreshTokenFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(RefreshTokenFilter.class);

    private final OAuth2AuthenticationService authenticationService;

    private final SecurityProperties securityProperties;

    public RefreshTokenFilter(OAuth2AuthenticationService authenticationService, SecurityProperties securityProperties) {
        this.authenticationService = authenticationService;
        this.securityProperties = securityProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            httpServletRequest = refreshTokensIfExpiring(httpServletRequest, httpServletResponse);
        } catch (Exception ex) {
            log.warn("Security exception: could not refresh tokens", ex);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Refresh the access and refresh tokens if they are about to expire.
     *
     * @param httpServletRequest  the servlet request holding the current cookies. If no refresh cookie is present,
     *                            then we are out of luck.
     * @param httpServletResponse the servlet response that gets the new set-cookie headers, if they had to be
     *                            refreshed.
     * @return a new request to use downstream that contains the new cookies, if they had to be refreshed.
     * @throws InvalidTokenException if the tokens could not be refreshed.
     */
    public HttpServletRequest refreshTokensIfExpiring(HttpServletRequest httpServletRequest, HttpServletResponse
        httpServletResponse) {
        HttpServletRequest newHttpServletRequest = httpServletRequest;

	    // 从上下文获取当前认证信息是否过期
        if (mustRefreshToken(httpServletRequest)) {
            // 从请求头部解析refreshToken
            String refreshToken = authenticationService.extractRefreshToken(httpServletRequest);
            if (StringUtils.isNotBlank(refreshToken)) {
                try {
                    newHttpServletRequest = authenticationService.refreshToken(httpServletRequest, httpServletResponse, refreshToken);
                } catch (Exception ex) {
                    log.error("刷新token失败：", ex);
                }
            }
        }
        return newHttpServletRequest;
    }

    /**
     * Check if we must refresh the access token.
     * We must refresh it, if it is about to expire.
     *
     * @param httpServletRequest
     * @return true, if it must be refreshed; false, otherwise.
     */
    private boolean mustRefreshToken(HttpServletRequest httpServletRequest) {
    	Object tokenExpiresIn = httpServletRequest.getAttribute(SecurityConstants.Security.RequestAttribute.tokenExpiresIn);
        // 找不到上下文，不去刷新
    	if (tokenExpiresIn == null) {
            return false;
        }

        // 判断将要过期，需要刷新
	    if ((Long) tokenExpiresIn < securityProperties.getSecurity().getClientAuthorization().getRefreshTokenExecuteInSeconds()) {
		    String accessToken = authenticationService.extractAccessToken(httpServletRequest);
    		log.info("access token即将过期需要刷新token：{}， {}", accessToken, tokenExpiresIn);
		    return true;
	    }

        return false;
    }
}
