package com.zeroone.tenancy.model;

public class TenancyTask {

    private String tenantCode;

    private Long currUseTime;

    public TenancyTask() {
    }

    public TenancyTask(String tenantCode, Long currUseTime) {
        this.tenantCode = tenantCode;
        this.currUseTime = currUseTime;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getCurrUseTime() {
        return currUseTime;
    }

    public void setCurrUseTime(Long currUseTime) {
        this.currUseTime = currUseTime;
    }
}
