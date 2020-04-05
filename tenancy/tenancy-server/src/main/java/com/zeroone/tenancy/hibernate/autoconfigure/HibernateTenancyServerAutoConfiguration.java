package com.zeroone.tenancy.hibernate.autoconfigure;

import com.zeroone.tenancy.hibernate.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.hibernate.repository.TenantDataSourceInfoRepository;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import javax.persistence.EntityManager;

/**
 * @author Charles
 * @since 2020-04-03
 */
@Configuration
@EnableConfigurationProperties({LiquibaseProperties.class})
public class HibernateTenancyServerAutoConfiguration {


    /**
     * create TenantDataSourceInfoRepository
     */
    @Bean
    public TenantDataSourceInfoRepository tenantDataSourceInfoRepository(EntityManager entityManager){

        JpaRepositoryFactoryBean<TenantDataSourceInfoRepository, TenantDataSourceInfo, Integer> factoryBean = new JpaRepositoryFactoryBean<>(TenantDataSourceInfoRepository.class);

        factoryBean.setEntityManager(entityManager);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
