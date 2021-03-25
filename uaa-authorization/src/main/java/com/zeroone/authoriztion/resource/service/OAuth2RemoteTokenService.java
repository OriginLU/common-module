package com.zeroone.authoriztion.resource.service;

import com.zeroone.authoriztion.resource.client.OAuth2TokenEndpointClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Oauth2RemoteTokenService
 * 自定义token加载
 */
@Service
public class OAuth2RemoteTokenService implements ResourceServerTokenServices {
	private final Logger log = LoggerFactory.getLogger(OAuth2RemoteTokenService.class);

	@Autowired
	private OAuth2TokenEndpointClient authorizationClient;

	@Autowired
	private AccessTokenConverter tokenConverter;


	@Override
	public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {

		Map<String, Object> map;
		try {
			map = authorizationClient.checkAccessToken(accessToken);
		} catch (Exception e) {
			log.error("check_token returned error: ", e);
			throw new InvalidTokenException(accessToken);
		}

		if (map.containsKey("error")) {
			log.error("check_token returned error: " + map.get("error"));
			throw new InvalidTokenException(accessToken);
		}

		// gh-838
		if (!Boolean.TRUE.equals(map.get("active"))) {
			log.error("check_token returned active attribute: " + map.get("active"));
			throw new InvalidTokenException(accessToken);
		}
		if (map.get(AccessTokenConverter.EXP) != null) {
			Long tokenExpiration = NumberUtils.toLong(String.valueOf(map.get(AccessTokenConverter.EXP)));
			Long tokenExpiresIn = tokenExpiration - (System.currentTimeMillis() / 1000);

//			HttpServletRequest request = RequestContextUtil.getRequest();
//			if (request != null) {
//				request.setAttribute(SecurityConstants.Security.RequestAttribute.tokenExpiration, tokenExpiration);
//				request.setAttribute(SecurityConstants.Security.RequestAttribute.tokenExpiresIn, tokenExpiresIn);
//			}
		}
		return tokenConverter.extractAuthentication(map);
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}
}
