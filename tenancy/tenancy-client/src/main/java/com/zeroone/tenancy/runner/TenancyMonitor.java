package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.enums.DatasourceStatus;
import com.zeroone.tenancy.model.DatasourceActionEvent;
import com.zeroone.tenancy.model.DatasourceMetrics;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenancyMonitor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String,DatasourceMetrics> metricsMap = new ConcurrentHashMap<>();

    private final Deque<DatasourceActionEvent> tasks = new ArrayDeque<>();


    public Map<String, DatasourceMetrics> getMetricsMap() {
        return metricsMap;
    }

    public void pushEvent(DatasourceActionEvent event){
        tasks.push(event);
    }


    @Scheduled(initialDelay = 10000L,fixedDelay = 5000L)
    void calculateMetrics(){

        for (DatasourceActionEvent event = tasks.poll();event != null;event = tasks.poll() ){
            String tenantCode = event.getTenantCode();
            TenantDataSourceProvider provider = (TenantDataSourceProvider) event.getSource();
            Long eventOccurredTime = event.getEventOccurredTime();
            Integer status = event.getStatus();

            if (status == DatasourceStatus.INIT.getStatus()){

                DatasourceMetrics metrics = new DatasourceMetrics();
                metrics.setTenantCode(tenantCode);
                metrics.setDataSourceInfo(provider.getDatasourceInfo(tenantCode));
                metrics.setInitTime(eventOccurredTime);
                metrics.setStatus(status);
                metricsMap.put(tenantCode,metrics);
            }

            if (status == DatasourceStatus.CREATE.getStatus()){

                DatasourceMetrics metrics = metricsMap.computeIfAbsent(tenantCode, (k) -> {
                    DatasourceMetrics datasourceMetrics = new DatasourceMetrics();
                    datasourceMetrics.setTenantCode(tenantCode);
                    datasourceMetrics.setDataSourceInfo(provider.getDatasourceInfo(tenantCode));
                    datasourceMetrics.setInitTime(eventOccurredTime);
                    datasourceMetrics.setStatus(status);
                    return datasourceMetrics;
                });
                metrics.setCreateTime(eventOccurredTime);
            }

            if (status == DatasourceStatus.RUNNING.getStatus()){
                if (!metricsMap.containsKey(tenantCode)) {
                    log.info("not found data source metrics info ：{}",tenantCode);
                    continue;
                }
                DatasourceMetrics metrics = metricsMap.get(tenantCode);
                metrics.addUseTimes();
                if (metrics.getCreateTime() == null) {
                    metrics.setCreateTime(eventOccurredTime);
                }
                metrics.setStatus(status);
                metrics.setRecentlyUseTime(eventOccurredTime);
            }

            if (status == DatasourceStatus.REMOVE.getStatus()){
                log.info("remove data source metrics info:{}",tenantCode);
                metricsMap.remove(tenantCode);
            }


        }


    }




}
