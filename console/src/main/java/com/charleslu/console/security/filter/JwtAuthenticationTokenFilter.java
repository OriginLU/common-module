package com.charleslu.console.security.filter;

import com.charleslu.console.security.config.WebSecurityConfig;
import com.charleslu.console.security.utils.JwtTokenHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Charles
 * @since 2020-02-01
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private JwtTokenHelper jwtTokenHelper;

    public JwtAuthenticationTokenFilter(JwtTokenHelper jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null){

            String token = resolveToken(request);

            if (StringUtils.hasText(token) && jwtTokenHelper.validToken(token)){

                //parse token to get user info
                Authentication authentication = jwtTokenHelper.getAuthentication(token);

                //save user info
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request,response);

    }

    /**
     * get token from header or parameter in request
     * @param request httpservletRequest
     * @return jwt token
     */
    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(WebSecurityConfig.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)){
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        String accessToken = request.getParameter(WebSecurityConfig.AUTHORIZATION_TOKEN);
        if (StringUtils.hasText(accessToken)){
            return accessToken;
        }
        return null;
    }
}
