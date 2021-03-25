package com.zeroone.authoriztion.resource.provider;


import com.zeroone.authoriztion.resource.dto.RoleDataAuthBindDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tyjuncai
 * @ClassName: SecurityUtils
 * @Description: Spring Security上下文用户信息操作工具类
 * @date 2018/12/21 11:07
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return
     */
    public static Optional<SecurityUser> getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof SecurityUser) {
                        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                        return securityUser;
                    }
                    return null;
                });
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    /**
     * 校验当前用户是否有某角色
     *
     * @param role
     * @return
     */
    public static boolean isCurrentUserInRole(String role) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role)))
                .orElse(false);
    }

    /**
     * 获取当前登录用户所属业务方标识
     *
     * @return
     */
    public static Optional<String> getCurrentUserTenantCode() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof SecurityUser) {
                        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                        return securityUser.getTenantCode();
                    }
                    return null;
                });
    }

    /**
     * 获取当前登录用户id
     *
     * @return
     */
    public static Optional<Long> getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof SecurityUser) {
                        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                        return securityUser.getUserId();
                    }
                    return null;
                });
    }

	/**
	 * 获取当前登录用户的角色
	 *
	 * @return
	 */
	public static Optional<Collection<GrantedAuthority>> getCurrentUserAuthorities() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication())
				.map(authentication -> {
					if (authentication instanceof UserAuthenticationToken) {
						UserAuthenticationToken userAuthenticationToken = (UserAuthenticationToken) authentication;
						Collection<GrantedAuthority> authorities = userAuthenticationToken.getAuthorities();
						return authorities;
					}
					return null;
				});
	}

    /**
     * 获取当前登录用户的数据权限
     *
     * @return
     */
    public static Optional<SecurityAuthority> getCurrentUserAuthority() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication instanceof UserAuthenticationToken) {
                        UserAuthenticationToken userAuthenticationToken = (UserAuthenticationToken) authentication;
                        return userAuthenticationToken.getSecurityAuthority();
                    }
                    return null;
                });
    }



	/**
	 * 获取当前登录用户的角色列表
	 */
	public static Optional<Set<Long>> getCurrentRoleIds(){
		Optional<SecurityAuthority> optional = getCurrentUserAuthority();
		return optional.map(o -> o.getDataAuthorityList().stream().map(RoleDataAuthBindDTO::getRoleId).collect(Collectors.toSet()));
	}
}
