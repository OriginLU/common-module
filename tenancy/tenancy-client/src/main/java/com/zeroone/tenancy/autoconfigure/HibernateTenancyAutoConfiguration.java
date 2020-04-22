package com.zeroone.tenancy.autoconfigure;

import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantIdentifierResolver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.persistence.EntityManager;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({JpaProperties.class})
@ConditionalOnClass({ LocalContainerEntityManagerFactoryBean.class, EntityManager.class })
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
