package com.zeroone.authoriztion.resource.filter;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.service.OAuth2AuthorityService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 校验权限过滤器加载装配
 */
public class ValidAuthorityFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private OAuth2AuthorityService authorityService;

	private AccessDeniedHandler accessDeniedHandler;

	private SecurityProperties securityProperties;

	public ValidAuthorityFilterConfigurer(OAuth2AuthorityService authorityService, AccessDeniedHandler accessDeniedHandler, SecurityProperties securityProperties) {
		this.authorityService = authorityService;
		this.accessDeniedHandler = accessDeniedHandler;
		this.securityProperties = securityProperties;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// 校验权限过滤器
		ValidAuthorityFilter validAuthorityFilter = new ValidAuthorityFilter(authorityService, accessDeniedHandler, securityProperties);
		http.addFilterAfter(validAuthorityFilter, OAuth2AuthenticationProcessingFilter.class);
	}
}
