package com.zeroone.tenancy.autoconfigure;


import com.google.common.collect.Lists;
import com.zeroone.tenancy.interceptor.TenantInterceptor;
import com.zeroone.tenancy.miss.handler.TenantCodeMissHandler;
import com.zeroone.tenancy.properties.TenancyClientProperties;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.runner.TenancyInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({LiquibaseProperties.class, TenancyClientProperties.class})
public class TenancyAutoConfiguration {


    @Bean
    public TenantDataSourceProvider tenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory){
        return new TenantDataSourceProvider(defaultListableBeanFactory);
    }


    /**
     * 多租户初始化器
     *  关于为什么使用RestTemplateCustomizer问题解读：
     *  <p>
     *   1.RestTemplate完成最后初始化需要在@see {@link AbstractApplicationContext#refresh()}中的finishBeanFactoryInitialization阶段操作，
     *   该阶段所处的bean的位置是在倒几位，这个时候初始化多租户的bean，会导致RestTemplate中LoadBalance相关的拦截器未加载，进而调用会
     *   出现直接失败，失败原因是未进行对应的服务名替换。</p>
     * <p>
     *   2.RestTemplateCustomizer使用详细可以查看@see{@link LoadBalancerAutoConfiguration#loadBalancedRestTemplateInitializerDeprecated(org.springframework.beans.factory.ObjectProvider)}，
     *   该方法实现了RestTemplate最后阶段的拦截器注入 </p>
     */
    @Bean
    @ConditionalOnBean({TenantDataSourceProvider.class})
    public TenancyInitializer tenancyInitializer(TenantDataSourceProvider tenantDataSourceProvider, ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers, TenancyClientProperties tenancyProperties){
        return new TenancyInitializer(tenantDataSourceProvider,restTemplateCustomizers,tenancyProperties);
    }


    /**
     * 配置多租户拦截器
     */
    @Slf4j
    @Configuration
    static class TenancyInterceptorConfiguration implements WebMvcConfigurer {


        /**
         * 需要过滤的链接
         */
        private final String[] excludes = {
                "/index.html",
                "/management/**",
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui**",
                "/webjars/**",
                "/error/**",
                "/api/multi-tenancy/**",
                "/health/**"
        };

        private TenantDataSourceProvider tenantDataSourceProvider;

        private TenancyClientProperties tenancyClientProperties;

        private Set<TenantCodeMissHandler> tenantCodeMissHandlers;

        private TenancyInitializer tenancyInitializer;

        public TenancyInterceptorConfiguration(TenantDataSourceProvider tenantDataSourceProvider,TenancyClientProperties tenancyClientProperties,TenancyInitializer tenancyInitializer, ObjectProvider<Set<TenantCodeMissHandler>> missHandlerProvider) {
            this.tenantDataSourceProvider = tenantDataSourceProvider;
            this.tenancyClientProperties = tenancyClientProperties;
            this.tenancyInitializer = tenancyInitializer;
            missHandlerProvider.ifAvailable(missHandler -> this.tenantCodeMissHandlers = missHandler);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {

            log.debug("add tenant interceptor");
            // 请求拦截
            List<String> allExcludes = Lists.newArrayList(excludes);
            String excludeUrls = tenancyClientProperties.getInterceptor().getExcludeUrls();
            if (StringUtils.hasText(excludeUrls)) {
                log.debug("server has config excludes of {}", excludeUrls);
                allExcludes.addAll(Arrays.asList(excludeUrls.split(",")));
            }
            registry.addInterceptor(new TenantInterceptor(tenantDataSourceProvider, tenantCodeMissHandlers,tenancyInitializer))
                    .addPathPatterns("/**").excludePathPatterns(allExcludes.toArray(new String[0]));
        }
    }

//    @Bean
//    @ConditionalOnBean({HibernateTenantDataSourceProvider.class})
//    @ConditionalOnProperty("spring.kafka.bootstrap-servers")
//    public TenantDataSourceKafkaListener tenantDataSourceKafkaListener(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider){
//        return new TenantDataSourceKafkaListener(hibernateTenantDataSourceProvider);
//    }
}
