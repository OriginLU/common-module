package com.charleslu.tenancy.multi.hibernate;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;

/**
 * 多租户数据源提供者
 *
 * @Author tanglh
 * @Date 2018/11/23 12:47
 */
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    /**
     * 默认数据源
     *
     * @return
     */
    @Override
    protected DataSource selectAnyDataSource() {
        return selectDataSource(TenantDataSourceProvider.DEFAULT_KEY);
    }

    /**
     * 自定义数据源
     * 不存在则返回默认
     *
     * @param tenantIdentifier
     * @return
     */
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return TenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
    }

}
