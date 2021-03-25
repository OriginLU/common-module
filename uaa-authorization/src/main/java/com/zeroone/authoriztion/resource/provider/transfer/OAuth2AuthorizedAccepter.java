package com.zeroone.authoriztion.resource.provider.transfer;

import com.zeroone.authoriztion.resource.provider.SecurityAuthority;
import com.zeroone.authoriztion.resource.provider.SecurityUser;

import javax.servlet.http.HttpServletRequest;

/**
 * 接收用户认证信息接口
 */
public interface OAuth2AuthorizedAccepter {

	/**
	 * 接收用户认证信息
	 * @param request
	 * @return
	 */
	SecurityUser receiveAuthentication(HttpServletRequest request);

	/**
	 * 接收用户权限信息
	 * @param request
	 * @return
	 */
	SecurityAuthority receiveAuthority(HttpServletRequest request);
}
