package com.zeroone.tenancy.hibernate.spi;


import javax.sql.DataSource;

public class HibernateTenantDataSourceContext {

    private static HibernateTenantDataSourceProvider PROVIDER;


    public static void setHibernateTenantDataSourceContext(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider) {
        PROVIDER = hibernateTenantDataSourceProvider;
    }



    public static DataSource getTenantDataSource(String tenantIdentifier){
        return PROVIDER.getDataSource(tenantIdentifier);
    }




}
