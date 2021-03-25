package com.zeroone.authoriztion.resource.provider;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  用户认证对象
 */
public class SecurityUser extends User {
	private static final long serialVersionUID = -3315416954031981402L;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户手机号
	 */
	private String phoneNum;

	/**
	 * 用户姓名
	 */
	private String realName;

	/**
	 * 用户类型
	 */
	private Integer type;

	/**
	 * 业务方标识
	 */
    private String tenantCode;

	/**
	 * 是否受权限控制
	 */
	private Integer ctrlType;

	/**
	 * 所在部门
	 */
	private List<Long> departmentIds;

	public SecurityUser() {
		super("null", "null", Collections.emptyList());
	}


	public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public SecurityUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public Integer getCtrlType() {
		return ctrlType;
	}

	public void setCtrlType(Integer ctrlType) {
		this.ctrlType = ctrlType;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}
}
