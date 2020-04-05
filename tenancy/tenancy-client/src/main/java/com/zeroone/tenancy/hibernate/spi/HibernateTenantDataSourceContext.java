package com.zeroone.tenancy.hibernate.spi;


import javax.sql.DataSource;

/**
 * 租户数据源上下文
 */
public class HibernateTenantDataSourceContext {


    private static HibernateTenantDataSourceContext INSTANCE;

    private final HibernateTenantDataSourceProvider provider;


    private HibernateTenantDataSourceContext(HibernateTenantDataSourceProvider provider) {
        this.provider = provider;
    }

    static void setHibernateTenantDataSourceContext(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider) {
        HibernateTenantDataSourceContext.INSTANCE = new HibernateTenantDataSourceContext(hibernateTenantDataSourceProvider);
    }


    public static DataSource getTenantDataSource(String tenantIdentifier){
        return INSTANCE.provider.getDataSource(tenantIdentifier);
    }



}
