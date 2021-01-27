package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.enums.DatasourceStatusEnum;
import com.zeroone.tenancy.dto.DatasourceMetrics;
import com.zeroone.tenancy.properties.TenancyClientProperties;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 租户心跳信息上送
 */
public class TenancyHealthChecker{

    private final Logger log = LoggerFactory.getLogger(getClass());


    private final TenantDataSourceProvider provider;

    private final TenancyMonitor tenancyMonitor;

    private final TenancyClientProperties tenancyClientProperties;

    private final RestTemplate restTemplate;

    public TenancyHealthChecker(TenantDataSourceProvider provider, TenancyMonitor tenancyMonitor, TenancyClientProperties tenancyClientProperties, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizer) {
        this.provider = provider;
        this.tenancyMonitor = tenancyMonitor;
        this.tenancyClientProperties = tenancyClientProperties;
        this.restTemplate = new RestTemplate();
        restTemplateCustomizer.ifAvailable(restTemplateCustomizers -> {
            for (RestTemplateCustomizer customizer : restTemplateCustomizers) {
                customizer.customize(restTemplate);
            }
        });
    }

    @Scheduled(initialDelay = 10000L,fixedDelay = 30000L)
    public void healthCheck(){


        Map<String, DatasourceMetrics> metricsMap = tenancyMonitor.getMetricsMap();
        if (CollectionUtils.isEmpty(metricsMap)){
            return;
        }
        pushTenancyMetrics(metricsMap);

        //空闲数据移除策略
        metricsMap.forEach((tenantCode,dataMetrics) -> {

            log.info("\n租户:{},\n初始化时间:{},\n创建时间:{},\n重新创建时间:{},\n最近一次使用时间：{},\n使用次数:{},\n运行状态:{}",
                    tenantCode,
                    getTime(dataMetrics.getInitTime()),
                    getTime(dataMetrics.getCreateTime()),
                    getTime(dataMetrics.getRecentlyOverrideTime()),
                    getTime(dataMetrics.getRecentlyUseTime()),
                    dataMetrics.getUseTimes(),
                    DatasourceStatusEnum.fromType(dataMetrics.getStatus()).getDesc());

            //默认数据源不可做删改
            if (dataMetrics.getTenantCode().equals(TenantIdentifierHelper.DEFAULT)) {
                return;
            }
            //执行空闲超时移除逻辑
            if (dataMetrics.getStatus() != DatasourceStatusEnum.RUNNING.getStatus()) {
                return;
            }
            long idleTime = System.currentTimeMillis() - dataMetrics.getRecentlyUseTime();
            Long retrieveTime = tenancyClientProperties.getRetrieveTime();
            if (retrieveTime == null){
                retrieveTime = TenancyClientProperties.DEFAULT_RETRIEVE_TIME;
            }else {
                retrieveTime = TimeUnit.MINUTES.toMillis(retrieveTime);
            }
            if (idleTime >= retrieveTime){
                log.info("idle time is over {} min,remove tenant [{}] datasource", TimeUnit.MILLISECONDS.toMinutes(retrieveTime),tenantCode);
                provider.remove(tenantCode);
            }

        });

    }

    /**
     * 上送数据源监控信息
     */
    private void pushTenancyMetrics(Map<String, DatasourceMetrics> metricsMap) {

        TenancyMetricsDTO tenancyMetricsDTO = new TenancyMetricsDTO();
        tenancyMetricsDTO.setMetricsMap(metricsMap);
        tenancyMetricsDTO.setInstanceId(tenancyClientProperties.getInstanceId());
        tenancyMetricsDTO.setInstanceName(tenancyClientProperties.getInstantName());
        tenancyMetricsDTO.setIp(tenancyClientProperties.getIp());
        tenancyMetricsDTO.setInstanceName(tenancyClientProperties.getInstanceId());

//        restTemplate.postForObject(getRequestUri())


    }

    public String getTime(Long time){

        if (time == null){
            return "-1";
        }
        return DateFormatUtils.format(new Date(time),"yyyy-MM-dd HH:mm:ss");
    }
}