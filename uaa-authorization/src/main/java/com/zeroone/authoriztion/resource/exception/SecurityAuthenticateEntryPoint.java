package com.zeroone.authoriztion.resource.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 鉴权失败入口
 */
public class SecurityAuthenticateEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
		Map<String, Object> map = new HashMap(4);
		map.put("error", "401");
		map.put("message", "您的账号已下线，请重新登录");
		map.put("path", httpServletRequest.getServletPath());
		map.put("timestamp", ZonedDateTime.now());
		httpServletResponse.setContentType("application/json");
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(httpServletResponse.getOutputStream(), map);
		} catch (Exception ex) {
//			throw SecurityBizException.build(SecurityErrorCode.error, "处理失败");
		}
	}
}
