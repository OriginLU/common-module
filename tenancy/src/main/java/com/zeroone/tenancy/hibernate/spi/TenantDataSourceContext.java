package com.zeroone.tenancy.hibernate.spi;


import javax.sql.DataSource;

public class TenantDataSourceContext {

    private static TenantDataSourceProvider PROVIDER;


    public static void setTenantDataSourceContext(TenantDataSourceProvider tenantDataSourceProvider) {
        PROVIDER = tenantDataSourceProvider;
    }



    public static DataSource getTenantDataSource(String tenantIdentifier){
        return PROVIDER.get(tenantIdentifier);
    }




}
