package com.zeroone.tenancy.hibernate.autoconfigure;


import com.zeroone.tenancy.hibernate.properties.TenancyClientProperties;
import com.zeroone.tenancy.hibernate.runner.HibernateTenancy;
import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({LiquibaseProperties.class, TenancyClientProperties.class})
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate",name = {"multiTenancy","tenant_identifier_resolver", "multi_tenant_connection_provider"})
public class HibernateTenancyAutoConfiguration {


    @Bean
    @Qualifier("loadBalanceRestTemplate")
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory){
        return new HibernateTenantDataSourceProvider(defaultListableBeanFactory);
    }


    /**
     * 多租户初始化器
     *  关于为什么使用RestTemplateCustomizer问题解读：
     *  <p>
     *   1.RestTemplate完成最后初始化需要在@see {@link AbstractApplicationContext#refresh()}中的finishBeanFactoryInitialization阶段操作，
     *   该阶段所处的bean的位置是在倒几位，这个时候初始化多租户的bean，会导致RestTemplate中LoadBalance相关的拦截器未加载，进而调用会
     *   出现直接失败，失败原因是未进行对应的服务名替换。</p>
     * <p>
     *   2.RestTemplateCustomizer使用详细可以查看@see{@link LoadBalancerAutoConfiguration#loadBalancedRestTemplateInitializerDeprecated(org.springframework.beans.factory.ObjectProvider)}，
     *   该方法实现了RestTemplate最后阶段的拦截器注入 </p>
     */
    @Bean
    @ConditionalOnBean({HibernateTenantDataSourceProvider.class})
    public HibernateTenancy hibernateTenancy(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers, TenancyClientProperties tenancyProperties){
        return new HibernateTenancy(hibernateTenantDataSourceProvider,restTemplateCustomizers,tenancyProperties);
    }

//    @Bean
//    @ConditionalOnBean({HibernateTenantDataSourceProvider.class})
//    @ConditionalOnProperty("spring.kafka.bootstrap-servers")
//    public TenantDataSourceKafkaListener tenantDataSourceKafkaListener(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider){
//        return new TenantDataSourceKafkaListener(hibernateTenantDataSourceProvider);
//    }
}
