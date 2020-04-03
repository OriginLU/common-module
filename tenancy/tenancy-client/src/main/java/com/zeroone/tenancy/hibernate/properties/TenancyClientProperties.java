package com.zeroone.tenancy.hibernate.properties;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Data
@Accessors(chain = true)
@ToString
@Configuration
@ConfigurationProperties(prefix = "tenancy.client", ignoreInvalidFields = true)
public class TenancyClientProperties implements BeanPostProcessor {

    private static final String INSTANCE_NAME_KEY = "spring.application.name";



    /**
     * 数据源服务名称
     */
    private String tenantServerName;

    /**
     * 实例名称
     */
    private String instantName;



    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!StringUtils.hasText(instantName)){
            this.instantName = EnvironmentHelper.getProperty(INSTANCE_NAME_KEY);
        }
        return bean;
    }



    @Configuration
    static class EnvironmentHelper implements EnvironmentAware{


        private static  Environment ENV;

        @Override
        public void setEnvironment(Environment environment) {
            ENV = environment;
        }

        public static String getProperty(String key){
            return ENV.getProperty(key);
        }

        public static String getProperty(String key,String defaultValue){
            return ENV.getProperty(key,defaultValue);
        }
    }

}
