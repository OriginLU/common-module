package com.zeroone.tenancy.demo.configuration;

import com.zeroone.tenancy.demo.controller.TestController;
import com.zeroone.tenancy.demo.entity.BankAccount;
import com.zeroone.tenancy.demo.repository.BankAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import javax.persistence.EntityManager;

@Configuration
public class JpaConfiguration {


    @Bean
    public BankAccountRepository bankAccountRepository(EntityManager entityManager){

        JpaRepositoryFactoryBean<BankAccountRepository, BankAccount, Long> factoryBean = new JpaRepositoryFactoryBean<>(BankAccountRepository.class);
        factoryBean.setEntityManager(entityManager);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }


    @Bean
    public TestController testController(){
        return new TestController();
    }

}
