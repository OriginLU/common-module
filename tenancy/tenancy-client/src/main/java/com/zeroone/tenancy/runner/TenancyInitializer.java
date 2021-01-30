package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.api.TenancyRemoteApi;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;


public class TenancyInitializer implements SmartInitializingSingleton {

    private final TenantDataSourceProvider provider;

    private final TenancyRemoteApi tenancyRemoteApi;


    public TenancyInitializer(TenantDataSourceProvider provider, TenancyRemoteApi tenancyRemoteApi) {
        this.provider = provider;
        this.tenancyRemoteApi = tenancyRemoteApi;
    }

    @Override
    public void afterSingletonsInstantiated() {
        //1.获取有效配置信息进行多租户的初始化
        List<DataSourceInfo> configs = tenancyRemoteApi.getAvailableConfigInfo();
        //2.启动默认执行数据库初始化操作
        provider.prepareDataSourceInfo(configs);
    }


    /**
     * 初始化租户数据源
     */
    public boolean initTenantDataSource(String tenantCode) {

        DataSourceInfo dataSourceInfo = tenancyRemoteApi.queryDataSource(tenantCode);
        provider.addDataSource(dataSourceInfo);
        return provider.hasDatasource(tenantCode);
    }
}
