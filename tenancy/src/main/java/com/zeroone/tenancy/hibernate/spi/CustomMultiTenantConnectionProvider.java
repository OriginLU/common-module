package com.zeroone.tenancy.hibernate.spi;

import com.zeroone.tenancy.hibernate.utils.TenantIdentifierHelper;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;

/**
 * 加载该类需要通过在配置文件中配置 @see {@link org.hibernate.cfg.AvailableSettings#MULTI_TENANT_CONNECTION_PROVIDER}
 * <pre>
 *     spring:
 *       jpa:
 *        properties:
 *         hibernate.tenant_identifier_resolver: com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider
 * </pre>
 * hibernate tenant spi implementations
 * 数据源选取
 */
public class CustomMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {


    @Override
    protected DataSource selectAnyDataSource() {
        return selectDataSource(TenantIdentifierHelper.DEFAULT);
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return TenantDataSourceContext.getTenantDataSource(tenantIdentifier);
    }
}
