package com.charleslu.tenancy.service.dto;


import java.io.Serializable;

/**
 * 租户映射传输对象
 */
public class TenantMappedDTO implements Serializable {

    private String tenantCode;

    private String businessId;

    private String businessCode;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public TenantMappedDTO() {
    }

    public TenantMappedDTO(String businessId, String businessCode) {
        this.businessId = businessId;
        this.businessCode = businessCode;
    }

    public TenantMappedDTO(String businessCode, String businessId, String tenantCode) {
        this.businessCode = businessCode;
        this.businessId = businessId;
        this.tenantCode = tenantCode;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TenantMappedDTO{");
        sb.append("tenantCode='").append(tenantCode).append('\'');
        sb.append(", businessId='").append(businessId).append('\'');
        sb.append(", businessCode='").append(businessCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
