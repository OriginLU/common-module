package com.zeroone.authoriztion.resource.client;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 *  OAuth2TokenEndpointClient
 *  oauth2 token相关接口
 * @see UaaTokenEndpointClient
 * @see OAuth2TokenEndpointClientAdapter
 */
public interface OAuth2TokenEndpointClient {

	/**
	 * 密码授权
	 * @param username
	 * @param password
	 * @param endpoint
	 * @param headerMap
	 * @return
	 */
    OAuth2AccessToken sendPasswordGrant(String username, String password, String endpoint, MultiValueMap<String, String> headerMap);

	/**
	 * 手机验证码授权
	 * @param phoneNum
	 * @param verifyCode
	 * @param endpoint
	 * @param headerMap
	 * @return
	 */
	OAuth2AccessToken sendVerifyCodeGrant(String phoneNum, String verifyCode, String endpoint, MultiValueMap<String, String> headerMap);

	/**
	 * 刷新token授权
	 * @param refreshTokenValue
	 * @return
	 */
	OAuth2AccessToken sendRefreshGrant(String refreshTokenValue);

	/**
	 * 校验token
	 * @param accessTokenValue
	 * @return
	 */
	Map<String, Object> checkAccessToken(String accessTokenValue);

	/**
	 * 取消token
	 * @param accessTokenValue
	 */
	void removeToken(String accessTokenValue);
}
