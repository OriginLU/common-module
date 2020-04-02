package com.zeroone.tenancy.hibernate.autoconfigure;


import com.zeroone.tenancy.hibernate.kafka.TenantDataSourceKafkaListener;
import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Order(-1)
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate", name = {"multiTenancy"})
public class HibernateTenancyAutoConfiguration {


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @ConditionalOnBean({SpringLiquibase.class})
    public HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory){
        return new HibernateTenantDataSourceProvider(defaultListableBeanFactory);
    }

    @Bean
    @ConditionalOnBean({HibernateTenantDataSourceProvider.class})
    @ConditionalOnProperty("spring.kafka.bootstrap-servers")
    public TenantDataSourceKafkaListener tenantDataSourceKafkaListener(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider){
        return new TenantDataSourceKafkaListener(hibernateTenantDataSourceProvider);
    }
}
