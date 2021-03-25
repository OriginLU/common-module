package com.zeroone.authoriztion.resource.dto;

import java.util.Objects;
import java.util.Set;

/**
 *  BackendAuthorityDTO
 *  后端权限DTO
 */
public class BackendAuthorityDTO {

    private Long id;

    private String url;

    private String method;

    private Set<Long> userIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (!(o instanceof BackendAuthorityDTO)) {
        	return false;
        }
        BackendAuthorityDTO that = (BackendAuthorityDTO) o;
        return Objects.equals(getUrl(), that.getUrl()) &&
                Objects.equals(getMethod(), that.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getMethod());
    }

    @Override
    public String toString() {
        return "BackendAuthorityDTO{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", userIds=" + userIds +
                '}';
    }
}
