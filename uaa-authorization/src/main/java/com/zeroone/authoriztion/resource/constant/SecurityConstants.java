package com.zeroone.authoriztion.resource.constant;

/**
 *  SecurityConstants
 * 常量类
 */
public interface SecurityConstants {

	interface Security {
		/**
		 * 客户端认证信息
		 */
		interface ClientAuthorization {
			String uaaUri = "http://uaaaccount";
			String uaaAuthorityUri = "http://uaaauthority";
			String accessTokenUri = "/oauth/token";
			String checkTokenUri = "/oauth/check_token";
			String removeTokenUri = "/api//account/logout";
			String authorityUsersUri = "/api/authority-users";
			String userDataAuthoritiesUri = "/api/users/data-authority";
			String userDepartmentIdsUri = "/api/users/department-ids";
			String tokenServiceId = "uaa";
			String clientId = "web_app";
			String clientSecret = "security_@123!";
			boolean allowedRibbon = Boolean.FALSE;
			boolean allowedReceive = Boolean.FALSE;
			long refreshTokenExecuteInSeconds = 2 * 24 * 60 * 60;
		}

		/**
		 * 请求头
		 */
		interface RequestHeader {
			String accessToken = "Authorization";
			String refreshToken = "Refresh";
		}

		/**
		 * 相应头
		 */
		interface ResponseHeader {
			String accessToken = "Access-token";
			String refreshToken = "Refresh-token";
		}

		/**
		 * 请求上下文属性
		 */
		interface RequestAttribute {
			String tokenExpiration = "tokenExpiration";
			String tokenExpiresIn = "tokenExpiresIn";
			String userAuthority = "userAuthority";
		}

		/**
		 * 认证数据转发头
		 */
		interface TransferHeader {
			String authenticationHeader = "oauth2-authentication";
			String authorityHeader = "oauth2-authority";
		}

		/**
		 * 授权模式
		 */
		interface GrantType {
			String password = "password";
			String verifyCode = "verify_code";
		}

		/**
		 * 授权入口
		 */
		interface Endpoint {
			String pc = "pc-web";
			String mobile = "mobile";
			String hfb_mobile = "hfb-mobile";
		}
	}
}
