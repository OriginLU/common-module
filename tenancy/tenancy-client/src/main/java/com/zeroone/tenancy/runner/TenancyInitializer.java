package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.constants.TenancyApiConstants;
import com.zeroone.tenancy.properties.TenancyClientProperties;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
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


public class TenancyInitializer implements SmartInitializingSingleton, InitializingBean {


    public static final HttpEntity<Void> DEFAULT_REQUEST = new HttpEntity<>(new HttpHeaders());

    private TenantDataSourceProvider provider;

    private RestTemplate restTemplate;

    private TenancyClientProperties tenancyClientProperties;

    private ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers;

    public TenancyInitializer(TenantDataSourceProvider provider, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers, TenancyClientProperties tenancyClientProperties) {
        this.provider = provider;
        this.restTemplateCustomizers = restTemplateCustomizers;
        this.tenancyClientProperties = tenancyClientProperties;
    }


    private List<DataSourceInfo> getAvailableConfigInfo() {

        return restTemplate.exchange(getRequestUri(tenancyClientProperties.getTenantServerName(), TenancyApiConstants.Query.QUERY_ALL_DATA_SOURCE, tenancyClientProperties.getInstantName()), HttpMethod.GET, DEFAULT_REQUEST, new ParameterizedTypeReference<List<DataSourceInfo>>() {
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

        //1.构造load balance的RestTemplate
        this.restTemplate = new RestTemplate();
        restTemplateCustomizers.ifAvailable(customizers -> {
            for (RestTemplateCustomizer customizer : customizers) {
                customizer.customize(restTemplate);
            }
        });
    }

    /**
     * 初始化是否成功
     */
    public boolean initTenantDataSource(String tenantCode) {

        return false;
    }
}
