package com.zeroone.authoriztion.resource.filter;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.provider.transfer.OAuth2AuthorizedAccepter;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *  解析认证信息过滤器加载装配
 */
public class ResolverAuthorizedFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private OAuth2AuthorizedAccepter authorizedAccepter;

	private SecurityProperties securityProperties;

	public ResolverAuthorizedFilterConfigurer(OAuth2AuthorizedAccepter authorizedAccepter, SecurityProperties securityProperties) {
		this.authorizedAccepter = authorizedAccepter;
		this.securityProperties = securityProperties;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// 解析认证信息过滤器
		ResolverAuthorizedFilter resolverAuthorizedFilter = new ResolverAuthorizedFilter(authorizedAccepter, securityProperties);
		http.addFilterAfter(resolverAuthorizedFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
