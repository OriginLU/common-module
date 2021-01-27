package com.zeroone.tenancy.api;

import com.zeroone.tenancy.properties.TenancyClientProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TenancyApi {



    private final TenancyClientProperties tenancyClientProperties;


    private final RestTemplate restTemplate;


    public TenancyApi(TenancyClientProperties tenancyClientProperties, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {
        this.tenancyClientProperties = tenancyClientProperties;
        this.restTemplate = new RestTemplate();
        restTemplateCustomizers.ifAvailable(customizers -> {
            for (RestTemplateCustomizer customizer : customizers) {
                customizer.customize(restTemplate);
            }
        });
    }
}
