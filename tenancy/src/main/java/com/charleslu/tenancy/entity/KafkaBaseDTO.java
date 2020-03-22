package com.charleslu.tenancy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * kafka基础数据传输对象
 *
 * @author ziyan
 * @email zhengmengyan@taoqicar.com
 * @date 2019年01月03日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaBaseDTO {

    /**
     * 租户号 | 业务方id
     */
    private String tenantCode;

    public String getTenantCode() {
        return tenantCode;
    }

    public KafkaBaseDTO setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"tenantCode\":\"").append(tenantCode).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
