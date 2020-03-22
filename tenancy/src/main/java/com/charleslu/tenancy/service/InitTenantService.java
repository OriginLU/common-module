package com.charleslu.tenancy.service;

import com.charleslu.tenancy.entity.DataSourceInfo;

/**
 * @Author tanglh
 * @Date 2018/12/12 12:56
 */
public interface InitTenantService {

    /**
     * 初始化租户信息
     *
     * @param dataSourceInfo 租户编码
     * @return true 初始化成功
     * false 初始化失败
     */
    boolean initTenantInfo(DataSourceInfo dataSourceInfo);

}