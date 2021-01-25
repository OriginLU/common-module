package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;

public class TenancyHealthChecker{

    private final Logger log = LoggerFactory.getLogger(getClass());


    private final TenantDataSourceProvider provider;

    public TenancyHealthChecker(TenantDataSourceProvider provider) {
        this.provider = provider;
    }



    @Scheduled(initialDelay = 10000L,fixedDelay = 30000L)
    public void healthCheck(){

        Map<String, DataSource> dataSourceMap = provider.getDataSourceMap();
        if (CollectionUtils.isEmpty(dataSourceMap)){
            return;
        }
        dataSourceMap.forEach((tenantCode,datasource) -> {
            log.info("tenancy health check:{}",tenantCode);
            //TODO tenancy health check
        });

    }
}
