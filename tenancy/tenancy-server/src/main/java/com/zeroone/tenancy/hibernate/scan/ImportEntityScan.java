package com.zeroone.tenancy.hibernate.scan;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * @author zero-one.lu
 * @since 2020-04-06
 */
public class ImportEntityScan implements BeanPostProcessor {


    private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;


    public ImportEntityScan(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        this.entityManagerFactoryBean = entityManagerFactoryBean;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

//        ((MutablePersistenceUnitInfo)persistenceUnitInfo).addManagedClassName(TenantDataSourceInfo.class.getName());

        return bean;
    }
}
