package com.zeroone.tenancy.hibernate.autoconfigure;

import com.zeroone.tenancy.hibernate.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.hibernate.repository.TenantDataSourceInfoRepository;
import com.zeroone.tenancy.hibernate.rest.TenantDataSourceConfigResource;
import com.zeroone.tenancy.hibernate.scan.ImportEntityScan;
import com.zeroone.tenancy.hibernate.service.TenantDataSourceInfoService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * @author Charles
 * @since 2020-04-03
 */
@Configuration
//@EntityScan("com.zeroone.tenancy.hibernate.entity")
//@EnableJpaRepositories("com.zeroone.tenancy.hibernate.repository")
public class HibernateTenancyServerAutoConfiguration {


    @Bean
    public ImportEntityScan importEntityScan( LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){

        return new ImportEntityScan(entityManagerFactoryBean);
    }

    /**
     * create TenantDataSourceInfoRepository
     */
    @Bean
    public TenantDataSourceInfoRepository tenantDataSourceInfoRepository(EntityManager entityManager, ObjectProvider<EntityPathResolver> resolver){

        JpaRepositoryFactoryBean<TenantDataSourceInfoRepository, TenantDataSourceInfo, Integer> factoryBean = new JpaRepositoryFactoryBean<>(TenantDataSourceInfoRepository.class);

        factoryBean.setEntityManager(entityManager);
        factoryBean.setEntityPathResolver(resolver);
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
