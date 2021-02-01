package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.api.TenancyRemoteApi;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.properties.TenancyClientConfig;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TenancyInitializer implements SmartInitializingSingleton {



    private final TenantDataSourceProvider provider;

    private final TenancyRemoteApi tenancyRemoteApi;

    private final TenancyClientConfig tenancyClientConfig;


    public TenancyInitializer(TenantDataSourceProvider provider, TenancyRemoteApi tenancyRemoteApi,TenancyClientConfig tenancyClientConfig) {
        this.provider = provider;
        this.tenancyRemoteApi = tenancyRemoteApi;
        this.tenancyClientConfig = tenancyClientConfig;
    }

    @Override
    public void afterSingletonsInstantiated() {

        Boolean sync = tenancyClientConfig.getSync();

        if (sync == null || sync){
            init();
        }else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    init();
                }finally {
                    executorService.shutdown();
                }
            });
        }
    }

    private void init() {
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
