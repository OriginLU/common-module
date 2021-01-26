package com.zeroone.tenancy.properties;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Accessors(chain = true)
@ToString
@Configuration
@ConfigurationProperties(prefix = "tenancy.client", ignoreInvalidFields = true)
public class TenancyClientProperties implements BeanPostProcessor, EnvironmentAware {


    public final static long DEFAULT_RETRIEVE_TIME = TimeUnit.HOURS.toMillis(1L);

    private final static  String INSTANCE_NAME_KEY = "spring.application.name";

    private final Interceptor interceptor = new Interceptor();

    private Environment environment;
    /**
     * 数据源服务名称
     */
    private String tenantServerName;

    /**
     * 实例名称
     */
    private String instantName;

    /**
     * 空闲回收时间
     */
    private Long retrieveTime;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!StringUtils.hasText(instantName)){
            this.instantName = getEnvironment().getProperty(INSTANCE_NAME_KEY);
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Data
    @Accessors(chain = true)
    @ToString
    public static class Interceptor{

        private List<String> excludeUrls;

    }

}
