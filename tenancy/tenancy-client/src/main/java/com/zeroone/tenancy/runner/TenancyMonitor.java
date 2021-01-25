package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.model.DatasourceActionEvent;
import com.zeroone.tenancy.model.DatasourceMetrics;
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

    private final Deque<DatasourceActionEvent> tasks = new ArrayDeque<>();


    public void add(DatasourceMetrics datasourceMetrics){
        metrics.put(datasourceMetrics.getTenantCode(),datasourceMetrics);
    }

    public void pushEvent(DatasourceActionEvent event){
        tasks.push(event);
    }


    @Scheduled(initialDelay = 10000L,fixedDelay = 5000L)
    public void calculateMetrics(){

        Collection<DatasourceMetrics> datasourceMetrics = metrics.values();
        if (CollectionUtils.isEmpty(datasourceMetrics)){
            return;
        }

        for (DatasourceActionEvent event = tasks.poll();event != null;event = tasks.poll() ){


        }


    }




}
