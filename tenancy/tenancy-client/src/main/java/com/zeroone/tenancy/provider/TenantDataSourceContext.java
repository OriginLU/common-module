package com.zeroone.tenancy.provider;


import javax.sql.DataSource;

/**
 * 租户数据源上下文
 */
public class TenantDataSourceContext {


    private static  TenantDataSourceContext INSTANCE;

    private final TenantDataSourceProvider provider;


    private TenantDataSourceContext(TenantDataSourceProvider provider) {
        this.provider = provider;
    }

    static void setTenantDataSourceContext(TenantDataSourceProvider tenantDataSourceProvider) {
        TenantDataSourceContext.INSTANCE = new TenantDataSourceContext(tenantDataSourceProvider);
    }


    public static DataSource getTenantDataSource(String tenantIdentifier){
        return INSTANCE.provider.getDataSource(tenantIdentifier);
    }



}
