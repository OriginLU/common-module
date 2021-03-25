package com.zeroone.authoriztion.resource.provider.transfer;

import javax.servlet.http.HttpServletRequest;

/**
 *  发送用户认证信息接口
 */
public interface OAuth2AuthorizedSender {

	/**
	 * 发送用户认证信息
	 * @param request
	 */
	void sendAuthorized(HttpServletRequest request);
}
