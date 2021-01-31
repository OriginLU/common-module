package com.zeroone.tenancy.rest;


import com.zeroone.tenancy.dto.TenancyMetricsDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenant/datasource")
public class TenantDataSourceResource {


    /**
     * 缓存数据信息
     */
    @PostMapping("/cache/datasourceMetrics")
    public void cacheDatasourceMetrics(@RequestBody TenancyMetricsDTO tenancyMetricsDTO){

    }

}
