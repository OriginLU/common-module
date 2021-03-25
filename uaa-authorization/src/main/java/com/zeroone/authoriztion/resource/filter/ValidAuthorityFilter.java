package com.zeroone.authoriztion.resource.filter;

import com.zeroone.authoriztion.resource.config.SecurityProperties;
import com.zeroone.authoriztion.resource.provider.SecurityUser;
import com.zeroone.authoriztion.resource.provider.SecurityUtils;
import com.zeroone.authoriztion.resource.service.OAuth2AuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.access.AccessDeniedHandler;
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
 *  校验权限过滤器
 */
public class ValidAuthorityFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(ValidAuthorityFilter.class);

    private final OAuth2AuthorityService authorityService;

    private final SecurityProperties securityProperties;

	private final AccessDeniedHandler accessDeniedHandler;

    public ValidAuthorityFilter(OAuth2AuthorityService authorityService, AccessDeniedHandler accessDeniedHandler, SecurityProperties securityProperties) {
        this.authorityService = authorityService;
        this.accessDeniedHandler = accessDeniedHandler;
        this.securityProperties = securityProperties;

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

	    // 获取当前登录用户
	    Optional<SecurityUser> securityUserOp = SecurityUtils.getCurrentUser();
	    final Boolean[] hasAuth = new Boolean[1];
//	    securityUserOp.ifPresent(securityUser -> {
//	    	if (ObjectUtils.equals(securityUser.getType(), UserType.business.getValue())) {
//			    List<BackendAuthorityDTO> backendAuthorityDTOList = authorityService.getAuthorityUserIdsMap();
//
//			    // 这里可能匹配到多条
//			    List<BackendAuthorityDTO> backendAuthorityDTOOpt = backendAuthorityDTOList.stream()
//						.filter(o -> StringUtils.isNotBlank(o.getUrl()))
//					    .filter(backendAuthDTO -> {
//						    RequestMatcher requestMatcher = new AntPathRequestMatcher(backendAuthDTO.getUrl(), backendAuthDTO.getMethod());
//						    return requestMatcher.matches(httpServletRequest);
//					    }).collect(Collectors.toList());
//
//				if (CollectionUtils.isEmpty(backendAuthorityDTOOpt)) {
//					hasAuth[0] = true;
//				} else {
//					// userId 做并集
//					Set<Long> hasAuthUserIds = new HashSet<>();
//					backendAuthorityDTOOpt.forEach(o -> {
//						if (CollectionUtils.isNotEmpty(o.getUserIds())) {
//							hasAuthUserIds.addAll(o.getUserIds());
//						}
//					});
//					hasAuthUserIds.remove(null);
//					hasAuth[0] = hasAuthUserIds.contains(securityUser.getUserId());
//				}
//			}
//		});
//
//	    if (BooleanUtils.isFalse(hasAuth[0])) {
//		    accessDeniedHandler.handle(httpServletRequest, httpServletResponse, new AccessDeniedException("Access is denied"));
//			return;
//	    }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
