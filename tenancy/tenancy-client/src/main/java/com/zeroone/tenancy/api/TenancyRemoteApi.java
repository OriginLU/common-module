package com.zeroone.tenancy.api;

import com.zeroone.tenancy.constants.TenancyApiConstants;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.enums.SelectStrategyEnum;
import com.zeroone.tenancy.properties.TenancyClientProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TenancyRemoteApi {




    public static final HttpEntity<Void> DEFAULT_REQUEST = new HttpEntity<>(new HttpHeaders());

    private final TenancyClientProperties tenancyClientProperties;

    private final RestTemplate restTemplate;

    private Boolean isEnableEnurekaLoadBalance = false;

    private LoadBalaceStrategy loadBalaceStrategy;

    private Supplier<String> url;


    public TenancyRemoteApi(TenancyClientProperties tenancyClientProperties, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {

        this.tenancyClientProperties = tenancyClientProperties;
        this.restTemplate = new RestTemplate();
        //检测是否启用ribbon
        restTemplateCustomizers.ifAvailable(customizers -> {
            this.isEnableEnurekaLoadBalance = true;
            String serverName = tenancyClientProperties.getServerName();
            if (StringUtils.isBlank(serverName)){
                throw new IllegalStateException("tenancy server name not found,check config please");
            }
            for (RestTemplateCustomizer customizer : customizers) {
                customizer.customize(restTemplate);
            }
            this.url = this.tenancyClientProperties::getServerName;
        });
        //
        if (!isEnableEnurekaLoadBalance){
            List<String> serverUrls = tenancyClientProperties.getServerUrls();
            if (CollectionUtils.isEmpty(serverUrls)){
                throw new IllegalStateException("tenancy server urls not found,check config please");
            }
            List<String> urls = new ArrayList<>();
            serverUrls.forEach(url -> {
                urls.add(url.replace("http：//","").trim());
            });

            SelectStrategyEnum strategy = tenancyClientProperties.getStrategy();
            if (strategy == null){
                this.loadBalaceStrategy = new RoundStrategy(urls);
            }else if (strategy.equals(SelectStrategyEnum.ROUND)){
                this.loadBalaceStrategy = new RoundStrategy(urls);
            }else {
                this.loadBalaceStrategy = new RandomStrategy(urls);
            }




            this.url = () -> loadBalaceStrategy.select();
        }

    }

    public List<DataSourceInfo> getAvailableConfigInfo() {

        return restTemplate.exchange(getRequestUri(TenancyApiConstants.Query.QUERY_ALL_DATA_SOURCE, tenancyClientProperties.getInstantName()), HttpMethod.GET, DEFAULT_REQUEST, new ParameterizedTypeReference<List<DataSourceInfo>>() {
        }).getBody();
    }

    public DataSourceInfo queryDataSource(String tenantCode){

        String requestUri = getRequestUri(TenancyApiConstants.Query.QUERY_TENANT_DATA_SOURCE, tenantCode,tenancyClientProperties.getInstantName(),"mysql");
        return restTemplate.exchange(requestUri, HttpMethod.GET, DEFAULT_REQUEST,DataSourceInfo.class).getBody();
    }

    private String getRequestUri(String uri, Object... replace) {

        if (!uri.startsWith("/")){
            uri = "/"+ uri;
        }

        return String.format("http://" + url.get() + uri, replace);
    }


    interface LoadBalaceStrategy{

        String select();
    }


    static class RoundStrategy implements LoadBalaceStrategy {


        private final String[] serverUrl;


        private final AtomicInteger count = new AtomicInteger(0);

        public RoundStrategy(List<String> serverUrl) {

            this.serverUrl = serverUrl.toArray(new String[0]);
        }

        @Override
        public String select() {
            return serverUrl[serverUrl.length % count.getAndIncrement()];
        }
    }


    static class RandomStrategy implements LoadBalaceStrategy{

        private final String[] serverUrl;

        private final Random random = new Random();


        private final AtomicInteger count = new AtomicInteger(0);

        public RandomStrategy(List<String> serverUrl) {
            this.serverUrl = serverUrl.toArray(new String[0]);
        }

        @Override
        public String select() {
            return serverUrl[random.nextInt(serverUrl.length - 1)];
        }
    }
}
