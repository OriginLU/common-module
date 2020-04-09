package com.zeroone.tenancy.autoconfigure;

import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantIdentifierResolver;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties({JpaProperties.class})
public class HibernateTenancyAutoConfiguration {

    /**
     * override bean to configure default multi-tenancy
     */
    @Bean
    public JpaProperties jpaProperties(JpaProperties jpaProperties){
        //添加多租户默认配置
        Map<String, String> properties = jpaProperties.getProperties();
        //配置多租户策略
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE.name());
        //配置多租户租户ID解析器
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, CustomMultiTenantIdentifierResolver.class.getName());
        //配置多租户ID链接提供器
        properties.putIfAbsent(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, CustomMultiTenantConnectionProvider.class.getName());

        return jpaProperties;
    }
}
