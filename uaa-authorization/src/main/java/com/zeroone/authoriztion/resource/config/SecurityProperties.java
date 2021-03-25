package com.zeroone.authoriztion.resource.config;

import com.zeroone.authoriztion.resource.constant.SecurityConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *  SecurityProperties
 * 配置类
 */
@Component
@ConfigurationProperties(prefix = "uaa", ignoreInvalidFields = true, ignoreUnknownFields = false)
public class SecurityProperties {

	private Security security;

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public static class Security {

		private ClientAuthorization clientAuthorization;

		public ClientAuthorization getClientAuthorization() {
			return clientAuthorization;
		}

		public void setClientAuthorization(ClientAuthorization clientAuthorization) {
			this.clientAuthorization = clientAuthorization;
		}

		public static class ClientAuthorization {
			private String uaaUri = SecurityConstants.Security.ClientAuthorization.uaaUri;

			private String uaaAuthorityUri = SecurityConstants.Security.ClientAuthorization.uaaAuthorityUri;

			private String accessTokenUri = SecurityConstants.Security.ClientAuthorization.accessTokenUri;

			private String checkTokenUri = SecurityConstants.Security.ClientAuthorization.checkTokenUri;

			private String removeTokenUri = SecurityConstants.Security.ClientAuthorization.removeTokenUri;

			private String authorityUsersUri = SecurityConstants.Security.ClientAuthorization.authorityUsersUri;

			private String userDataAuthoritiesUri = SecurityConstants.Security.ClientAuthorization.userDataAuthoritiesUri;

			private String userDepartmentIdsUri = SecurityConstants.Security.ClientAuthorization.userDepartmentIdsUri;

			private String tokenServiceId = SecurityConstants.Security.ClientAuthorization.tokenServiceId;

			private String clientId = SecurityConstants.Security.ClientAuthorization.clientId;

			private String clientSecret = SecurityConstants.Security.ClientAuthorization.clientSecret;

			private Boolean allowedRibbon = SecurityConstants.Security.ClientAuthorization.allowedRibbon;

			private Boolean allowedReceive = SecurityConstants.Security.ClientAuthorization.allowedReceive;

			private Long refreshTokenExecuteInSeconds = SecurityConstants.Security.ClientAuthorization.refreshTokenExecuteInSeconds;

			public String getUaaUri() {
				return uaaUri;
			}

			public void setUaaUri(String uaaUri) {
				this.uaaUri = uaaUri;
			}

			public String getUaaAuthorityUri() {
				return uaaAuthorityUri;
			}

			public void setUaaAuthorityUri(String uaaAuthorityUri) {
				this.uaaAuthorityUri = uaaAuthorityUri;
			}

			public String getAccessTokenUri() {
				return accessTokenUri;
			}

			public void setAccessTokenUri(String accessTokenUri) {
				this.accessTokenUri = accessTokenUri;
			}

			public String getCheckTokenUri() {
				return checkTokenUri;
			}

			public void setCheckTokenUri(String checkTokenUri) {
				this.checkTokenUri = checkTokenUri;
			}

			public String getRemoveTokenUri() {
				return removeTokenUri;
			}

			public void setRemoveTokenUri(String removeTokenUri) {
				this.removeTokenUri = removeTokenUri;
			}

			public String getAuthorityUsersUri() {
				return authorityUsersUri;
			}

			public void setAuthorityUsersUri(String authorityUsersUri) {
				this.authorityUsersUri = authorityUsersUri;
			}

			public String getUserDataAuthoritiesUri() {
				return userDataAuthoritiesUri;
			}

			public void setUserDataAuthoritiesUri(String userDataAuthoritiesUri) {
				this.userDataAuthoritiesUri = userDataAuthoritiesUri;
			}

			public String getUserDepartmentIdsUri() {
				return userDepartmentIdsUri;
			}

			public void setUserDepartmentIdsUri(String userDepartmentIdsUri) {
				this.userDepartmentIdsUri = userDepartmentIdsUri;
			}

			public String getTokenServiceId() {
				return tokenServiceId;
			}

			public void setTokenServiceId(String tokenServiceId) {
				this.tokenServiceId = tokenServiceId;
			}

			public String getClientId() {
				return clientId;
			}

			public void setClientId(String clientId) {
				this.clientId = clientId;
			}

			public String getClientSecret() {
				return clientSecret;
			}

			public void setClientSecret(String clientSecret) {
				this.clientSecret = clientSecret;
			}

			public Boolean getAllowedRibbon() {
				return allowedRibbon;
			}

			public void setAllowedRibbon(Boolean allowedRibbon) {
				this.allowedRibbon = allowedRibbon;
			}

			public Boolean getAllowedReceive() {
				return allowedReceive;
			}

			public void setAllowedReceive(Boolean allowedReceive) {
				this.allowedReceive = allowedReceive;
			}

			public Long getRefreshTokenExecuteInSeconds() {
				return refreshTokenExecuteInSeconds;
			}

			public void setRefreshTokenExecuteInSeconds(Long refreshTokenExecuteInSeconds) {
				this.refreshTokenExecuteInSeconds = refreshTokenExecuteInSeconds;
			}
		}
	}
}
