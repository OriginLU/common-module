package com.zeroone.authoriztion.resource.client;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 *  UaaTokenEndpointClient
 *  oauth2 token相关接口实现
 */
@Component
public class UaaTokenEndpointClient extends OAuth2TokenEndpointClientAdapter implements OAuth2TokenEndpointClient {

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
			Boolean allowedRibbon = securityProperties.getSecurity().getClientAuthorization().getAllowedRibbon();
			if (BooleanUtils.isTrue(allowedRibbon)) {
				setProperties(loadBalancedRestTemplate, securityProperties);
			} else {
				setProperties(vanillaRestTemplate, securityProperties);
			}
		} else {
			setProperties(vanillaRestTemplate, securityProperties);
		}
    }

    @Override
    protected void addAuthentication(HttpHeaders reqHeaders, MultiValueMap<String, String> formParams) {
        reqHeaders.add("Authorization", getAuthorizationHeader());
    }

    /**
     * 设置通用请求头Basic authorization header
     * @return
     */
    protected String getAuthorizationHeader() {
        String clientId = getClientId();
        String clientSecret = getClientSecret();
        String authorization = clientId + ":" + clientSecret;
        return "Basic " + Base64Utils.encodeToString(authorization.getBytes(StandardCharsets.UTF_8));
    }
}
