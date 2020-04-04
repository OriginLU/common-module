package com.zeroone.tenancy.hibernate.autoconfigure;

import com.zeroone.tenancy.hibernate.repository.TenantDataSourceInfoRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;

/**
 * @author Charles
 * @since 2020-04-03
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages= { "com.zeroone.tenancy.hibernate.repository" })
@EnableConfigurationProperties({LiquibaseProperties.class})
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate",name = {"multiTenancy","tenant_identifier_resolver", "multi_tenant_connection_provider"})
public class HibernateTenancyServerAutoConfiguration {


    public TenantDataSourceInfoRepository tenantDataSourceInfoRepository(EntityManager entityManager){

        JpaRepositoryFactoryBean<TenantDataSourceInfoRepository, TenantDataSourceInfoRepository, Integer> factoryBean = new JpaRepositoryFactoryBean<>(TenantDataSourceInfoRepository.class);

        factoryBean.setEntityManager(entityManager);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
