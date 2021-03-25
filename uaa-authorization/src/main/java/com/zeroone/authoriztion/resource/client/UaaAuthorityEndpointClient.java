package com.zeroone.authoriztion.resource.client;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.provider.SecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 *  UaaAuthorityEndpointClient
 *  oauth2 权限相关接口实现
 */
@Component
public class UaaAuthorityEndpointClient extends OAuth2AuthorityEndpointClientAdapter implements OAuth2AuthorityEndpointClient {

    @Autowired
    @Qualifier("oAuth2LoadBalancedRestTemplate")
    private RestTemplate loadBalancedRestTemplate;

    @Autowired
    @Qualifier("oAuth2VanillaRestTemplate")
    private RestTemplate vanillaRestTemplate;

    @Autowired
    private SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        if (null != securityProperties.getSecurity()) {
            Optional.ofNullable(securityProperties.getSecurity().getClientAuthorization()).ifPresent(o -> {
                if (BooleanUtils.isTrue(o.getAllowedRibbon())) {
                    setProperties(loadBalancedRestTemplate, securityProperties);
                } else {
                    setProperties(vanillaRestTemplate, securityProperties);
                }
            });
        } else {
            setProperties(vanillaRestTemplate, securityProperties);
        }
    }

    @Override
    protected void addTenantNo(HttpHeaders reqHeaders, MultiValueMap<String, String> formParams) {
        Optional<String> optional = SecurityUtils.getCurrentUserTenantCode();
//        optional.ifPresent(tenantNo -> reqHeaders.add(Constants.TENANT_CODE_KEY, tenantNo));
    }
}
