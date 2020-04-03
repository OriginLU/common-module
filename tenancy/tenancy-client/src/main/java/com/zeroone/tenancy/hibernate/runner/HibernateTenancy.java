package com.zeroone.tenancy.hibernate.runner;

import com.zeroone.tenancy.hibernate.constants.TenancyApiConstants;
import com.zeroone.tenancy.hibernate.model.DataSourceInfo;
import com.zeroone.tenancy.hibernate.properties.TenancyProperties;
import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class HibernateTenancy implements SmartInitializingSingleton, InitializingBean {


    public static final HttpEntity<Void> DEFAULT_REQUEST = new HttpEntity<>(new HttpHeaders());

    private HibernateTenantDataSourceProvider provider;

    private RestTemplate restTemplate;

    private TenancyProperties tenancyProperties;

    private ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers;

    public HibernateTenancy(HibernateTenantDataSourceProvider provider, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers, TenancyProperties tenancyProperties) {
        this.provider = provider;
        this.restTemplateCustomizers = restTemplateCustomizers;
        this.tenancyProperties = tenancyProperties;
    }


    private List<DataSourceInfo> getAvailableConfigInfo() {

        return restTemplate.exchange(getRequestUri(tenancyProperties.getTenantServerName(), TenancyApiConstants.Query.QUERY_ALL_DATA_SOURCE, tenancyProperties.getInstantName()), HttpMethod.GET, DEFAULT_REQUEST, new ParameterizedTypeReference<List<DataSourceInfo>>() {
        }).getBody();
    }

    private String getRequestUri(String serverName, String uri, Object... replace) {
        return String.format("http://" + serverName + uri, replace);
    }


    @Override
    public void afterSingletonsInstantiated() {
        //1.获取有效配置信息进行多租户的初始化
        List<DataSourceInfo> configs = getAvailableConfigInfo();
        //2.初始化
        configs.forEach(provider::addDataSource);
    }

    @Override
    public void afterPropertiesSet() {
        this.restTemplate = new RestTemplate();
        restTemplateCustomizers.ifAvailable(customizers -> {
            for (RestTemplateCustomizer customizer : customizers) {
                customizer.customize(restTemplate);
            }
        });
    }
}
