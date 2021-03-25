package com.zeroone.authoriztion.resource.client;

import com.zeroone.authoriztion.resource.dto.BackendAuthorityDTO;
import com.zeroone.authoriztion.resource.dto.RoleDataAuthBindDTO;

import java.util.List;

/**
 * OAuth2AuthorityEndpointClient
 * oauth2 权限相关接口
 * @see UaaAuthorityEndpointClient
 * @see OAuth2AuthorityEndpointClientAdapter
 */
public interface OAuth2AuthorityEndpointClient {

	/**
	 * 获取后端权限对应的用户id集合
	 * @return
	 */
	List<BackendAuthorityDTO> getAuthorityUserIdsMap();

	/**
	 * 获取用户所有数据权限
	 * @param userId
	 * @return
	 */
	List<RoleDataAuthBindDTO> getUserDataAuthorityList(Long userId);

	/**
	 * 获取用户所在部门
	 * @param userId
	 * @return
	 */
	List<Long> getUserDepartmentIdList(Long userId);
}
