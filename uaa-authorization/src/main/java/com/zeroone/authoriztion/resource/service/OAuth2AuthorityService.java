package com.zeroone.authoriztion.resource.service;

import com.zeroone.authoriztion.resource.client.OAuth2AuthorityEndpointClient;
import com.zeroone.authoriztion.resource.dto.BackendAuthorityDTO;
import com.zeroone.authoriztion.resource.dto.RoleDataAuthBindDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  用户权限相关业务逻辑
 */
@Service
public class OAuth2AuthorityService {

    @Autowired
    private OAuth2AuthorityEndpointClient authorityEndpointClient;

	/**
	 * 获取后端权限以及对应用户列表
	 * @return
	 */
	public List<BackendAuthorityDTO> getAuthorityUserIdsMap() {
        return authorityEndpointClient.getAuthorityUserIdsMap();
    }

	/**
	 * 获取用户所有数据权限
	 * @param userId
	 * @return
	 */
	public List<RoleDataAuthBindDTO> getUserDataAuthorityList(Long userId) {
		return authorityEndpointClient.getUserDataAuthorityList(userId);
	}

	/**
	 * 获取用户所在部门
	 * @param userId
	 * @return
	 */
	public List<Long> getUserDepartmentIdList(Long userId) {
		return authorityEndpointClient.getUserDepartmentIdList(userId);
	}
}
