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
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "oauth2", name = "mode", havingValue = "client-jdbc")
public class JdbcTokenConfiguration {


    //令牌管理服务
    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices(TokenStore tokenStore,ClientDetailsService clientDetailsService) {

        DefaultTokenServices service = new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService);//客户端详情服务
        //支持刷新令牌
        service.setSupportRefreshToken(true);
        //令牌存储策略 使用jdbc
        service.setTokenStore(tokenStore);
        return service;
    }

    @Bean
    public ClientDetailsService clientDetailsService(@Qualifier("dataSource") DataSource dataSource) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(new BCryptPasswordEncoder());
        return clientDetailsService;
    }



    @Bean
    public AuthorizationCodeServices authorizationCodeServices(@Qualifier("dataSource")DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);//设置授权码模式的授权码如何存取
    }


    /**
     * oauth2.0 token 持久化服务使用，数据库保存
     */
    @Bean
    public TokenStore tokenStore(DataSource dataSource){
        return new JdbcTokenStore(dataSource);
    }


}
