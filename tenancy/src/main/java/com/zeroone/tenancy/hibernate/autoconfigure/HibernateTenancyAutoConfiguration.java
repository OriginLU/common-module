package com.zeroone.tenancy.hibernate.autoconfigure;


import com.zeroone.tenancy.hibernate.kafka.TenantDataSourceKafkaListener;
import com.zeroone.tenancy.hibernate.spi.TenantDataSourceProvider;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

@Order(-1)
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate", name = {"multiTenancy"})
public class HibernateTenancyAutoConfiguration {



    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @ConditionalOnBean({SpringLiquibase.class})
    public TenantDataSourceProvider tenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory){
        return new TenantDataSourceProvider(defaultListableBeanFactory);
    }

    @Bean
    @ConditionalOnBean({TenantDataSourceProvider.class})
    @ConditionalOnProperty("spring.kafka.bootstrap-servers")
    public TenantDataSourceKafkaListener tenantDataSourceKafkaListener(TenantDataSourceProvider tenantDataSourceProvider){
        return new TenantDataSourceKafkaListener(tenantDataSourceProvider);
    }
}
