package com.zeroone.uaa.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * <p>token endpoint @see {@link org.springframework.security.oauth2.provider.endpoint.TokenEndpoint}</p>
 * <p>Authorization endpoint @see {@link org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint}</p>
 */
@Configuration
@ConditionalOnProperty(prefix = "oauth2", name = "authorization-mode", havingValue = "jwt")
public class JwtTokenConfiguration {


    /**
     * 令牌管理服务
     */
    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices(TokenStore tokenStore, ClientDetailsService clientDetailsService, JwtAccessTokenConverter jwtAccessTokenConverter) {
        DefaultTokenServices service = new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService);//客户端详情服务
        //支持刷新令牌
        service.setSupportRefreshToken(true);
        //令牌存储策略 使用jwt还是
        service.setTokenStore(tokenStore);
        //令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(jwtAccessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        // 令牌默认有效期2小时
        service.setAccessTokenValiditySeconds(7200);
        // 刷新令牌默认有效期3天
        service.setRefreshTokenValiditySeconds(259200);
        return service;
    }


    @Bean
    public AuthorizationCodeServices authorizationCodeServices(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);//设置授权码模式的授权码如何存取
    }


    /**
     * 使用jwt做token持久化服务，token无需存储
     */
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
        return new JwtTokenStore(accessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("1234567890");
        return jwtAccessTokenConverter;
    }


    @Bean
    public ClientDetailsService clientDetailsService(@Qualifier("dataSource") DataSource dataSource) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(new BCryptPasswordEncoder());
        return clientDetailsService;
    }


}
