package com.zeroone.authoriztion.resource.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *  拒绝访问控制
 */
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		Map<String, Object> map = new HashMap(4);
		map.put("error", "417");
		map.put("message", "无访问权限");
		map.put("timestamp", ZonedDateTime.now());
		map.put("path", request.getServletPath());
		response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
		response.setContentType("application/json;charset=UTF-8");
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), map);
		} catch (Exception ex) {
//			throw SecurityBizException.build(SecurityErrorCode.error, "处理失败");
		}
	}
}
