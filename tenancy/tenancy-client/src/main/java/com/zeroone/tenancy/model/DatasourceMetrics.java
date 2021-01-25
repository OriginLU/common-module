package com.zeroone.tenancy.model;

import com.zeroone.tenancy.dto.DataSourceInfo;

/**
 * 数据库使用情况
 */
public class DatasourceMetrics {


    /**
     * 租户号
     */
    private String tenantCode;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最近一次使用时间
     */
    private Long recentlyUseTime;

    /**
     * 使用次数
     */
    private int useTimes = 0;

    /**
     * 数据源信息
     */
    private DataSourceInfo dataSourceInfo;


    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getRecentlyUseTime() {
        return recentlyUseTime;
    }

    public void setRecentlyUseTime(Long recentlyUseTime) {
        this.recentlyUseTime = recentlyUseTime;
    }

    public Integer getUseTimes() {
        return useTimes;
    }

    public void addUseTimes(){
        this.useTimes ++;
    }



    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

    public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }
}
