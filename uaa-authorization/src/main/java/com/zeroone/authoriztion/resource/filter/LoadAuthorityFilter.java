package com.zeroone.authoriztion.resource.filter;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.provider.SecurityAuthority;
import com.zeroone.authoriztion.resource.provider.SecurityUser;
import com.zeroone.authoriztion.resource.provider.SecurityUtils;
import com.zeroone.authoriztion.resource.provider.UserAuthenticationToken;
import com.zeroone.authoriztion.resource.service.OAuth2AuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * LoadAuthorityFilter
 */
public class LoadAuthorityFilter extends GenericFilterBean {

	private final Logger log = LoggerFactory.getLogger(ValidAuthorityFilter.class);

	private final OAuth2AuthorityService authorityService;

	private final SecurityProperties securityProperties;

	public LoadAuthorityFilter(OAuth2AuthorityService authorityService, SecurityProperties securityProperties) {
		this.authorityService = authorityService;
		this.securityProperties = securityProperties;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		try {
			// 获取当前登录用户
			Optional<SecurityUser> securityUserOp = SecurityUtils.getCurrentUser();
			if (securityUserOp.isPresent()) {
				SecurityUser securityUser = securityUserOp.get();
				SecurityAuthority securityAuthority = new SecurityAuthority();

//				if (ObjectUtils.equals(securityUser.getType(), UserType.business.getValue())) {
//
//					// 获取用户所在部门
//					List<Long> departmentIdList = authorityService.getUserDepartmentIdList(securityUser.getUserId());
//					if (CollectionUtils.isNotEmpty(departmentIdList)) {
//						securityUser.setDepartmentIds(departmentIdList);
//					}
//					// 获取用户角色和数据权限
//					List<RoleDataAuthBindDTO> dataAuthorityDTOList = authorityService.getUserDataAuthorityList(securityUser.getUserId());
//					if (CollectionUtils.isNotEmpty(dataAuthorityDTOList)) {
//						// 判读是否存在不受权限控制的角色
//						List<RoleDataAuthBindDTO> notCtrlList = dataAuthorityDTOList.stream().filter(item -> ObjectUtils.equals(item.getCtrlType(), CtrlType
//								.CTRL_DISABLE.getValue())).collect(Collectors.toList());
//						if (CollectionUtils.isNotEmpty(notCtrlList)) {
//							securityUser.setCtrlType(CtrlType.CTRL_DISABLE.getValue());
//						}
//						securityAuthority.setDataAuthorityList(dataAuthorityDTOList);
//					}
//				}

				// 重新封装认证信息
				UserAuthenticationToken userAuthenticationToken = new UserAuthenticationToken(securityUser, "", securityUser.getAuthorities());
				userAuthenticationToken.setSecurityAuthority(securityAuthority);
				SecurityContextHolder.getContext().setAuthentication(userAuthenticationToken);
			}
		} catch (Exception e) {
			log.error("LoadAuthorityFilter error:", e);
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
