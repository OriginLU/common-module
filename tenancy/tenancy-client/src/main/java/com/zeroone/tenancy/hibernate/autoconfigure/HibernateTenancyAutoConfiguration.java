package com.zeroone.tenancy.hibernate.autoconfigure;


import com.zeroone.tenancy.hibernate.properties.TenancyClientProperties;
import com.zeroone.tenancy.hibernate.runner.HibernateTenancy;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantIdentifierResolver;
import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({LiquibaseProperties.class, TenancyClientProperties.class,JpaProperties.class})
public class HibernateTenancyAutoConfiguration {



    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public JpaProperties jpaProperties(JpaProperties jpaProperties){
        //2.添加多租户默认配置
        Map<String, String> properties = jpaProperties.getProperties();
        //配置多租户策略
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE.name());
        //配置多租户租户ID解析器
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, CustomMultiTenantIdentifierResolver.class.getName());
        //配置多租户ID链接提供器
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, CustomMultiTenantConnectionProvider.class.getName());
        return jpaProperties;
    }

    @Bean
    @Qualifier("loadBalanceRestTemplate")
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    @Bean
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
