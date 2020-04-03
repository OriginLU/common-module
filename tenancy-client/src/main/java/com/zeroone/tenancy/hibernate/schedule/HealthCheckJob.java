package com.zeroone.tenancy.hibernate.schedule;

import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

public class HealthCheckJob {


    private RestTemplate restTemplate;

    private HibernateTenantDataSourceProvider provider;




    public HealthCheckJob(HibernateTenantDataSourceProvider provider,RestTemplate restTemplate) {
        this.provider = provider;
        this.restTemplate = restTemplate;
    }



    @Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60)
    public void healthCheck(){


    }
}
