package com.zeroone.tenancy.hibernate.autoconfigure;

import com.zeroone.tenancy.hibernate.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.hibernate.repository.TenantDataSourceInfoRepository;
import com.zeroone.tenancy.hibernate.rest.TenantDataSourceConfigResource;
import com.zeroone.tenancy.hibernate.service.TenantDataSourceInfoService;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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

    /**
     * create TenantDataSourceInfoService
     */
    @Bean
    @ConditionalOnBean(TenantDataSourceInfoRepository.class)
    public TenantDataSourceInfoService tenantDataSourceInfoService(){
        return new TenantDataSourceInfoService();
    }

    @Bean
    @ConditionalOnBean(TenantDataSourceInfoService.class)
    public TenantDataSourceConfigResource tenantDataSourceConfigResource(){

        return new TenantDataSourceConfigResource();
    }


}
