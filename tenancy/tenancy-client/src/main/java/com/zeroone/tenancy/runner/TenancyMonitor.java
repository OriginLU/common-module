package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.model.DatasourceMetrics;
import com.zeroone.tenancy.model.TenancyTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenancyMonitor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String,DatasourceMetrics> metrics = new ConcurrentHashMap<>();

    private final Deque<TenancyTask> tasks = new ArrayDeque<>();


    public void add(DatasourceMetrics datasourceMetrics){
        metrics.put(datasourceMetrics.getTenantCode(),datasourceMetrics);
    }

    public void remove(String tenantCode){
        metrics.remove(tenantCode);
    }


    public void pushTask(String tenantCode){
        tasks.push(new TenancyTask(tenantCode,System.currentTimeMillis()));
    }


    @Scheduled(initialDelay = 10000L,fixedDelay = 5000L)
    public void calculateMetrics(){

        Collection<DatasourceMetrics> datasourceMetrics = metrics.values();
        if (CollectionUtils.isEmpty(datasourceMetrics)){
            return;
        }

        for (TenancyTask tenancyTask = tasks.poll();tenancyTask != null;tenancyTask = tasks.poll() ){

            String tenantCode = tenancyTask.getTenantCode();
            if (!metrics.containsKey(tenantCode)) {
                log.warn("not found tenancy metrics:{}",tenantCode);
                continue;
            }
            DatasourceMetrics dbMetrics = metrics.get(tenantCode);
            dbMetrics.addUseTimes();
            dbMetrics.setRecentlyUseTime(tenancyTask.getCurrUseTime());
        }


    }




}
