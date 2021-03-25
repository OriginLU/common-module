package com.zeroone.authoriztion.resource.filter;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.service.OAuth2AuthenticationService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 *  刷新TOKEN过滤器加载装配
 */
public class RefreshTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private OAuth2AuthenticationService authenticationService;

	private SecurityProperties securityProperties;

	public RefreshTokenFilterConfigurer(OAuth2AuthenticationService authenticationService, SecurityProperties securityProperties) {
		this.authenticationService = authenticationService;
		this.securityProperties = securityProperties;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// 刷新Token过滤器
		RefreshTokenFilter refreshTokenFilter = new RefreshTokenFilter(authenticationService, securityProperties);
		http.addFilterAfter(refreshTokenFilter, LoadAuthorityFilter.class);
	}
}
