package com.zeroone.uaa.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * 客户端模式[jwt 无需客户端存储]
 * @author zero-one.lu
 * @since 2020-12-05
 */
@Configuration
@EnableAuthorizationServer
@ConditionalOnProperty(prefix = "oauth2",name = "authorization-mode",havingValue = "jwt")
public class JwtAuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private ClientDetailsService clientDetailsService;



    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;


    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();

    }



    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
       endpoints.allowedTokenEndpointRequestMethods(HttpMethod.POST)
               .authenticationManager(authenticationManager)
               .tokenServices(authorizationServerTokenServices)
               .authorizationCodeServices(authorizationCodeServices);
    }




}
