package com.zeroone.authoriztion.resource.service;

import com.zeroone.authoriztion.resource.client.OAuth2TokenEndpointClient;
import com.zeroone.authoriztion.resource.constant.SecurityConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 用户认证相关业务逻辑
 */
@Service
public class OAuth2AuthenticationService {

    private final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationService.class);

	private final static List<String> endpointList = Arrays.asList(
		SecurityConstants.Security.Endpoint.pc,
		SecurityConstants.Security.Endpoint.mobile,
		SecurityConstants.Security.Endpoint.hfb_mobile
	);
	private static Map<String, String> clientHeaderMapping = new HashMap<>(5);
    static {
	    clientHeaderMapping.put("clientV", "client_v");
	    clientHeaderMapping.put("clientOSV", "os_v");
	    clientHeaderMapping.put("mobileModel", "os_name");
	    clientHeaderMapping.put("uuid", "uuid");
	    clientHeaderMapping.put("deviceId", "device_id");
    }

    @Autowired
    private OAuth2TokenEndpointClient authorizationClient;

    /**
     * Authenticate the user by username and password.
     *
     * @param request  the request coming from the client.
     * @param response the response going back to the server.
     * @param params   the params holding the username, password and rememberMe.
     * @return the OAuth2AccessToken as a ResponseEntity. Will return OK (200), if successful.
     * If the UAA cannot authenticate the user, the status code returned by UAA will be returned.
     */
    public ResponseEntity<OAuth2AccessToken> authenticate(HttpServletRequest request, HttpServletResponse response,
                                                          Map<String, String> params) {
        try {
        	// 请求入参
            String username = params.get("username");
            String password = params.get("password");
            String phoneNum = params.get("phoneNum");
            String verifyCode = params.get("verifyCode");
	        String grantType = params.get("grantType") == null ? SecurityConstants.Security.GrantType.password : params.get("grantType");
	        String endpoint = params.get("endpoint");
			if (StringUtils.isBlank(endpoint) || !endpointList.contains(endpoint)) {
//				throw SecurityBizException.build(SecurityErrorCode.login_error);
			}
	        // 解析请求头
	        MultiValueMap<String, String> headerMap = extractClientRequestHeader(request);

	        // 授权模式决定请求方式
            OAuth2AccessToken accessToken;
            if (StringUtils.equals(grantType, SecurityConstants.Security.GrantType.password)) {
	            accessToken = authorizationClient.sendPasswordGrant(username, password, endpoint, headerMap);
            } else {
	            accessToken = authorizationClient.sendVerifyCodeGrant(phoneNum, verifyCode, endpoint, headerMap);
            }

            if (log.isDebugEnabled()) {
                log.debug("successfully authenticated user {}", params.get("username"));
            }
            return ResponseEntity.ok(accessToken);
        } catch (Exception ex) {
            log.error("failed to get OAuth2 tokens from UAA", ex);
            return null;
//            throw SecurityBizException.build(SecurityErrorCode.login_error, ex.getMessage());
        }
    }

    /**
     * Try to refresh the access token using the refresh token provided as cookie.
     * Note that browsers typically send multiple requests in parallel which means the access token
     * will be expired on multiple threads. We don't want to send multiple requests to UAA though,
     * so we need to cache results for a certain duration and synchronize threads to avoid sending
     * multiple requests in parallel.
     *
     * @param request       the request potentially holding the refresh token.
     * @param response      the response setting the new cookies (if refresh was successful).
     * @param refreshToken  the refresh token cookie. Must not be null.
     * @return the new servlet request containing the updated cookies for relaying downstream.
     */
    public HttpServletRequest refreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {

        synchronized (refreshToken) {
            //check if we have a result from another thread already
	        OAuth2AccessToken accessToken = authorizationClient.sendRefreshGrant(refreshToken);

            // 刷新token后回写response
	        response.addHeader(SecurityConstants.Security.ResponseHeader.accessToken, accessToken.getValue());
	        response.addHeader(SecurityConstants.Security.ResponseHeader.refreshToken, accessToken.getRefreshToken().getValue());
            return new HttpServletRequestWrapper(request);
        }
    }


	/**
	 * Logs the user out by clearing all cookies.
	 *
	 * @param httpServletRequest  the request containing the Cookies.
	 * @param httpServletResponse the response used to clear them.
	 */
	public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String accessToken = extractAccessToken(httpServletRequest);
		authorizationClient.removeToken(accessToken);
	}

	/**
	 * Extract the OAuth bearer token.
	 *
	 * @param request The request.
	 * @return The token, or null if no OAuth authorization header was supplied.
	 */
	public String extractAccessToken(HttpServletRequest request) {
		// first check the header...
		String token = extractAccessTokenFromHeader(request);
		// bearer type allows a request parameter as well
		if (token == null) {
			token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
		}
		return token;
	}

	/**
	 * Extract the OAuth bearer token from a header.
	 *
	 * @param request The request.
	 * @return The token, or null if no OAuth authorization header was supplied.
	 */
	public String extractAccessTokenFromHeader(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders(SecurityConstants.Security.RequestHeader.accessToken);
		// typically there is only one (most servers enforce that)
		while (headers.hasMoreElements()) {
			String value = headers.nextElement();
			if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
				String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}

		return null;
	}

	/**
	 * Extract the OAuth bearer token.
	 *
	 * @param request The request.
	 * @return The token, or null if no OAuth authorization header was supplied.
	 */
	public String extractRefreshToken(HttpServletRequest request) {
		// first check the header...
		String token = extractRefreshTokenFromHeader(request);
		// bearer type allows a request parameter as well
		if (token == null) {
			token = request.getParameter(OAuth2AccessToken.REFRESH_TOKEN);
		}
		return token;
	}

	/**
	 * Extract the OAuth bearer token from a header.
	 *
	 * @param request The request.
	 * @return The token, or null if no OAuth authorization header was supplied.
	 */
	public String extractRefreshTokenFromHeader(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders(SecurityConstants.Security.RequestHeader.refreshToken);
		// typically there is only one (most servers enforce that)
		while (headers.hasMoreElements()) {
			String value = headers.nextElement();
			int commaIndex = value.indexOf(',');
			if (commaIndex > 0) {
				value = value.substring(0, commaIndex);
			}
			return value;
		}
		return null;
	}

	/**
	 * Extract the client request header.
	 *
	 * @param request The request.
	 * @return the client request header map
	 */
	public MultiValueMap<String, String> extractClientRequestHeader(HttpServletRequest request) {
		MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
		for (String key : clientHeaderMapping.keySet()) {
			String header = clientHeaderMapping.get(key);
			String value = StringUtils.isBlank(request.getHeader(header)) ? "" : request.getHeader(header);
			headerMap.set(key, value);
		}
		return headerMap;
	}
}
