package com.zeroone.tenancy.hibernate.runner;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.hibernate.constants.TenancyApiConstants;
import com.zeroone.tenancy.hibernate.properties.TenancyClientProperties;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantIdentifierResolver;
import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


public class HibernateTenancy implements SmartInitializingSingleton, InitializingBean {


    public static final HttpEntity<Void> DEFAULT_REQUEST = new HttpEntity<>(new HttpHeaders());

    private HibernateTenantDataSourceProvider provider;

    private RestTemplate restTemplate;

    private JpaProperties jpaProperties;

    private TenancyClientProperties tenancyClientProperties;

    private ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers;

    public HibernateTenancy(HibernateTenantDataSourceProvider provider, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers, JpaProperties jpaProperties, TenancyClientProperties tenancyClientProperties) {
        this.provider = provider;
        this.restTemplateCustomizers = restTemplateCustomizers;
        this.jpaProperties = jpaProperties;
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
        //2.添加多租户默认配置
        Map<String, String> properties = this.jpaProperties.getProperties();
        //配置多租户策略
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE.name());
        //配置多租户租户ID解析器
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, CustomMultiTenantIdentifierResolver.class.getName());
        //配置多租户ID链接提供器
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, CustomMultiTenantConnectionProvider.class.getName());
    }
}
