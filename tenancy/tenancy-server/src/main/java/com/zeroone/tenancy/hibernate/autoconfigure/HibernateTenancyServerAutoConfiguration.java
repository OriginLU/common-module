package com.zeroone.tenancy.hibernate.autoconfigure;

import com.zeroone.tenancy.hibernate.rest.TenantDataSourceConfigResource;
import com.zeroone.tenancy.hibernate.service.TenantDataSourceInfoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Charles
 * @since 2020-04-03
 */
@Configuration
@EntityScan("com.zeroone.tenancy.hibernate.entity")
@EnableJpaRepositories("com.zeroone.tenancy.hibernate.repository")
public class HibernateTenancyServerAutoConfiguration {


//    /**
//     * create TenantDataSourceInfoRepository
//     */
//    @Bean
//    public TenantDataSourceInfoRepository tenantDataSourceInfoRepository(EntityManager entityManager, ObjectProvider<EntityPathResolver> resolver){
//
//        JpaRepositoryFactoryBean<TenantDataSourceInfoRepository, TenantDataSourceInfo, Integer> factoryBean = new JpaRepositoryFactoryBean<>(TenantDataSourceInfoRepository.class);
//
//        factoryBean.setEntityManager(entityManager);
//        factoryBean.setEntityPathResolver(resolver);
//        factoryBean.afterPropertiesSet();
//        return factoryBean.getObject();
//    }

    /**
     * create TenantDataSourceInfoService
     */
    @Bean
    public TenantDataSourceInfoService tenantDataSourceInfoService(){
        return new TenantDataSourceInfoService();
    }

    @Bean
    @ConditionalOnBean(TenantDataSourceInfoService.class)
    public TenantDataSourceConfigResource tenantDataSourceConfigResource(){

        return new TenantDataSourceConfigResource();
    }


}
