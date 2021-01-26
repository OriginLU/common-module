package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.enums.DatasourceStatus;
import com.zeroone.tenancy.model.DatasourceMetrics;
import com.zeroone.tenancy.properties.TenancyClientProperties;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TenancyHealthChecker{

    private final Logger log = LoggerFactory.getLogger(getClass());


    private final TenantDataSourceProvider provider;

    private final TenancyMonitor tenancyMonitor;

    private final TenancyClientProperties tenancyClientProperties;

    public TenancyHealthChecker(TenantDataSourceProvider provider, TenancyMonitor tenancyMonitor,TenancyClientProperties tenancyClientProperties) {
        this.provider = provider;
        this.tenancyMonitor = tenancyMonitor;
        this.tenancyClientProperties = tenancyClientProperties;
    }

    @Scheduled(initialDelay = 10000L,fixedDelay = 30000L)
    public void healthCheck(){


        Map<String, DatasourceMetrics> metricsMap = tenancyMonitor.getMetricsMap();
        if (CollectionUtils.isEmpty(metricsMap)){
            return;
        }

        metricsMap.forEach((tenantCode,dataMetrics) -> {

            log.info("\n租户:{},\n初始化时间:{},\n创建时间:{},\n最近一次使用时间：{},\n使用次数:{},\n运行状态:{}",
                    tenantCode,
                    getTime(dataMetrics.getInitTime()),
                    getTime(dataMetrics.getCreateTime()),
                    getTime(dataMetrics.getRecentlyUseTime()),
                    dataMetrics.getUseTimes(),
                    DatasourceStatus.fromType(dataMetrics.getStatus()).getDesc());

            //TODO send health beat to server


            //执行空闲超时移除逻辑
            if (dataMetrics.getStatus() != DatasourceStatus.RUNNING.getStatus() && !dataMetrics.getTenantCode().equals(TenantIdentifierHelper.DEFAULT)) {
                return;
            }
            long idleTime = System.currentTimeMillis() - dataMetrics.getRecentlyUseTime();
            Long retrieveTime = tenancyClientProperties.getRetrieveTime();
            if (retrieveTime == null){
                retrieveTime = TenancyClientProperties.DEFAULT_RETRIEVE_TIME;
            }
            if (idleTime >= retrieveTime){
                log.info("idle time is over {} min,remove tenant [{}] datasource", TimeUnit.MILLISECONDS.toMinutes(retrieveTime),tenantCode);
                provider.remove(tenantCode);
            }

        });

    }

    public String getTime(Long time){
        if (time == null){
            return "-1";
        }
        return DateFormatUtils.format(new Date(time),"yyyy-MM-dd HH:mm:ss");
    }
}
