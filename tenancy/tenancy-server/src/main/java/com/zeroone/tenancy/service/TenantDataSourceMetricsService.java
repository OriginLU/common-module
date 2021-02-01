package com.zeroone.tenancy.service;

import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import com.zeroone.tenancy.dto.TenancyMetricsQueryDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TenantDataSourceMetricsService {



    private final Map<String, TenancyMetricsDTO> tenancyMetricsDTOMap = new ConcurrentHashMap<>();



    public void cacheTenancyMetrics(TenancyMetricsDTO tenancyMetricsDTO){

        String instanceId = tenancyMetricsDTO.getInstanceId();
        String instanceName = tenancyMetricsDTO.getInstanceName();
        String key = instanceName + "-" + instanceId;

        tenancyMetricsDTOMap.put(key,tenancyMetricsDTO);
    }


    public List<TenancyMetricsDTO> queryTenancyMetrics(TenancyMetricsQueryDTO tenancyMetricsQueryDTO){

        return null;
    }




}
