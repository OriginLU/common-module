package com.zeroone.tenancy.runner;

import com.zeroone.tenancy.constants.TenancyApiConstants;
import com.zeroone.tenancy.dto.DataSourceInfo;
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

    private final TenantDataSourceProvider provider;

    private RestTemplate restTemplate;

    private final TenancyClientProperties tenancyClientProperties;

    private final ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers;

    public TenancyInitializer(TenantDataSourceProvider provider, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers, TenancyClientProperties tenancyClientProperties) {
        this.provider = provider;
        this.restTemplateCustomizers = restTemplateCustomizers;
        this.tenancyClientProperties = tenancyClientProperties;
    }


    private List<DataSourceInfo> getAvailableConfigInfo() {

        return restTemplate.exchange(getRequestUri(TenancyApiConstants.Query.QUERY_ALL_DATA_SOURCE, tenancyClientProperties.getInstantName()), HttpMethod.GET, DEFAULT_REQUEST, new ParameterizedTypeReference<List<DataSourceInfo>>() {
        }).getBody();
    }

    private String getRequestUri(String uri, Object... replace) {
        return String.format("http://" + tenancyClientProperties.getTenantServerName() + uri, replace);
    }


    @Override
    public void afterSingletonsInstantiated() {
        //1.获取有效配置信息进行多租户的初始化
        List<DataSourceInfo> configs = getAvailableConfigInfo();
        //2.启动默认执行数据库初始化操作
        provider.prepareDataSourceInfo(configs);
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
     * 初始化租户数据源
     */
    public boolean initTenantDataSource(String tenantCode) {

        String requestUri = getRequestUri(TenancyApiConstants.Query.QUERY_TENANT_DATA_SOURCE, tenancyClientProperties.getInstantName(),"mysql");
        DataSourceInfo dataSourceInfo = restTemplate.exchange(requestUri, HttpMethod.GET, DEFAULT_REQUEST,DataSourceInfo.class).getBody();
        provider.addDataSource(dataSourceInfo);
        return provider.hasDatasource(tenantCode);
    }
}
