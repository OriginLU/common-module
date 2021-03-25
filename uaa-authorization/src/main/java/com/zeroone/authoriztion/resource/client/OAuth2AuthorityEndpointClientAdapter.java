package com.zeroone.authoriztion.resource.client;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.dto.BackendAuthorityDTO;
import com.zeroone.authoriztion.resource.dto.RoleDataAuthBindDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 *  OAuth2AuthorityEndpointClientAdapter
 *  oauth2 权限相关接口基础实现
 */
public abstract class OAuth2AuthorityEndpointClientAdapter implements OAuth2AuthorityEndpointClient {
    private final Logger log = LoggerFactory.getLogger(OAuth2AuthorityEndpointClientAdapter.class);
    protected RestTemplate restTemplate;
    protected SecurityProperties securityProperties;

    private static final ParameterizedTypeReference<List<RoleDataAuthBindDTO>> LIST_ROLE_DATA_AUTH_BIND_RESPONSE = new ParameterizedTypeReference<List<RoleDataAuthBindDTO>>() {
    };

    private static final ParameterizedTypeReference<List<BackendAuthorityDTO>> LIST_BACKEND_AUTHORITY_RESPONSE = new ParameterizedTypeReference<List<BackendAuthorityDTO>>() {
    };

	private static final ParameterizedTypeReference<List<Long>> LIST_LONG_RESPONSE = new ParameterizedTypeReference<List<Long>>() {
	};

    /**
     * 属性设置
     *
     * @param restTemplate
     * @param securityProperties
     */
    protected void setProperties(RestTemplate restTemplate, SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<BackendAuthorityDTO> getAuthorityUserIdsMap() {
        String url = getAuthorityUsersMapEndpoint();
        HttpHeaders headers = new HttpHeaders();
        addTenantNo(headers, null);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<BackendAuthorityDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, LIST_BACKEND_AUTHORITY_RESPONSE);
        List<BackendAuthorityDTO> resultList = responseEntity.getBody();
        log.info("getAuthorityRoleMap result success.");
        return resultList;
    }

    @Override
    public List<RoleDataAuthBindDTO> getUserDataAuthorityList(Long userId) {
        String url = getUserDataAuthoritiesEndpoint() + "?userId=" + String.valueOf(userId);
	    HttpHeaders headers = new HttpHeaders();
	    addTenantNo(headers, null);
	    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<RoleDataAuthBindDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, LIST_ROLE_DATA_AUTH_BIND_RESPONSE);
        List<RoleDataAuthBindDTO> result = responseEntity.getBody();
        log.info("getDataAuthorityList result: {}", result);
        return result;
    }

	@Override
	public List<Long> getUserDepartmentIdList(Long userId) {
		String url = getUserDepartmentIdsEndpoint() + "?userId=" + String.valueOf(userId);
		HttpHeaders headers = new HttpHeaders();
		addTenantNo(headers, null);
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(null, headers);
		ResponseEntity<List<Long>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, LIST_LONG_RESPONSE);
		List<Long> result = responseEntity.getBody();
		log.info("getUserDepartmentIdList result: {}", result);
		return result;
	}

    /**
     * 添加业务方租户头部
     *
     * @param reqHeaders
     * @param formParams
     */
    protected abstract void addTenantNo(HttpHeaders reqHeaders, MultiValueMap<String, String> formParams);

    /**
     * 获取权限对应用户集合入口
     *
     * @return
     */
    protected String getAuthorityUsersMapEndpoint() {
        String uaaAuthorityUrl = securityProperties.getSecurity().getClientAuthorization().getUaaAuthorityUri();
        String authorityRolesUri = securityProperties.getSecurity().getClientAuthorization().getAuthorityUsersUri();
        if (StringUtils.isBlank(uaaAuthorityUrl) || StringUtils.isBlank(authorityRolesUri)) {
//            throw SecurityBizException.build(SecurityErrorCode.base_data_error, "没有配置获取权限对应角色集合入口");
        }
        return uaaAuthorityUrl + authorityRolesUri;
    }

    /**
     * 获取用户数据权限集合入口
     *
     * @return
     */
    protected String getUserDataAuthoritiesEndpoint() {
        String uaaAuthorityUrl = securityProperties.getSecurity().getClientAuthorization().getUaaAuthorityUri();
        String dataAuthoritiesUri = securityProperties.getSecurity().getClientAuthorization().getUserDataAuthoritiesUri();
        if (StringUtils.isBlank(uaaAuthorityUrl) || StringUtils.isBlank(dataAuthoritiesUri)) {
//            throw SecurityBizException.build(SecurityErrorCode.base_data_error, "没有配置获取用户数据权限集合入口");
        }
        return uaaAuthorityUrl + dataAuthoritiesUri;
    }

	/**
	 * 获取用户所在部门列表入口
	 *
	 * @return
	 */
	protected String getUserDepartmentIdsEndpoint() {
		String uaaAuthorityUrl = securityProperties.getSecurity().getClientAuthorization().getUaaAuthorityUri();
		String dataAuthoritiesUri = securityProperties.getSecurity().getClientAuthorization().getUserDepartmentIdsUri();
		if (StringUtils.isBlank(uaaAuthorityUrl) || StringUtils.isBlank(dataAuthoritiesUri)) {
//			throw SecurityBizException.build(SecurityErrorCode.base_data_error, "没有配置获取用户所在部门列表入口");
		}
		return uaaAuthorityUrl + dataAuthoritiesUri;
	}
}
