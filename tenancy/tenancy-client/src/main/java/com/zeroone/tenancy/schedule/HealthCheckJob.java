package com.zeroone.tenancy.schedule;

import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

public class HealthCheckJob {


    private RestTemplate restTemplate;

    private TenantDataSourceProvider provider;




    public HealthCheckJob(TenantDataSourceProvider provider, RestTemplate restTemplate) {
        this.provider = provider;
        this.restTemplate = restTemplate;
    }



    @Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60)
    public void healthCheck(){


    }
}
