package com.zeroone.tenancy.autoconfigure;


import com.google.common.collect.Lists;
import com.zeroone.tenancy.annotation.TenancyApi;
import com.zeroone.tenancy.aop.TenancyDataSourceAspect;
import com.zeroone.tenancy.event.DatasourceEventListener;
import com.zeroone.tenancy.event.DatasourceEventPublisher;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantConnectionProvider;
import com.zeroone.tenancy.hibernate.spi.CustomMultiTenantIdentifierResolver;
import com.zeroone.tenancy.interceptor.TenantInterceptor;
import com.zeroone.tenancy.miss.handler.TenantCodeMissHandler;
import com.zeroone.tenancy.mybatis.datasource.RoutingDataSource;
import com.zeroone.tenancy.properties.TenancyClientProperties;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.runner.TenancyHealthChecker;
import com.zeroone.tenancy.runner.TenancyInitializer;
import com.zeroone.tenancy.runner.TenancyMonitor;
import com.zeroone.tenancy.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({LiquibaseProperties.class, TenancyClientProperties.class})
public class TenancyAutoConfiguration {


    @Bean
    public TenancyMonitor tenancyMonitor(){
        return new TenancyMonitor();
    }

    @Bean
    public DatasourceEventPublisher datasourceEventPublisher(ApplicationEventPublisher applicationEventPublisher){
        return new DatasourceEventPublisher(applicationEventPublisher);
    }

    @Bean
    public DatasourceEventListener datasourceEventListener(TenancyMonitor tenancyMonitor){
        return new DatasourceEventListener(tenancyMonitor);
    }

    @Bean
    public TenantDataSourceProvider tenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory, DataSource dataSource){
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


    @Bean
    public TenancyDataSourceAspect tenancyDataSourceAspect(TenantDataSourceProvider tenantDataSourceProvider){
        return new TenancyDataSourceAspect(tenantDataSourceProvider);
    }

    @Bean
    public TenancyHealthChecker tenancyHealthChecker(TenantDataSourceProvider provider,TenancyMonitor tenancyMonitor,TenancyClientProperties tenancyClientProperties){
        return new TenancyHealthChecker(provider,tenancyMonitor,tenancyClientProperties);
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
                "/tenancy/**",
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

        private final TenantDataSourceProvider tenantDataSourceProvider;

        private final TenancyClientProperties tenancyClientProperties;

        private final TenancyInitializer tenancyInitializer;

        private Set<TenantCodeMissHandler> tenantCodeMissHandlers;


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
            List<String> excludeUrls = tenancyClientProperties.getInterceptor().getExcludeUrls();
            if (!CollectionUtils.isEmpty(excludeUrls)) {
                log.debug("server has config excludes of {}", excludeUrls);
                allExcludes.addAll(excludeUrls);
            }
            registry.addInterceptor(new TenantInterceptor(tenantDataSourceProvider, tenantCodeMissHandlers,tenancyInitializer))
                    .addPathPatterns("/**").excludePathPatterns(allExcludes.toArray(new String[0]));
        }
    }

    /**
     * api注册
     */
    @Configuration
    static class ApiResourceConfiguration implements ApplicationRunner {


        private final static String DETECT_HANDLER_METHODS = "detectHandlerMethods";

        private final static String TENANCY_PACKAGE = "com.zeroone.tenancy";

        private final ApplicationContext applicationContext;

        private final RequestMappingHandlerMapping requestMappingHandlerMapping;

        private final Method registerMethod;


        public ApiResourceConfiguration(ApplicationContext applicationContext, RequestMappingHandlerMapping requestMappingHandlerMapping) throws Exception{
            this.applicationContext = applicationContext;
            this.requestMappingHandlerMapping = requestMappingHandlerMapping;
            this.registerMethod = AbstractHandlerMethodMapping.class.getDeclaredMethod(DETECT_HANDLER_METHODS, Object.class);
            this.registerMethod.setAccessible(true);
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {

            //扫描需要注册api
            Set<Class<?>> classes = ResourceUtils.getClasses(TENANCY_PACKAGE, TenancyApi.class);
            if (CollectionUtils.isEmpty(classes)){
                return;
            }
            //api注册
            for (Class<?> constructClass : classes) {
                Constructor<?> constructor = constructClass.getConstructor();
                Object handler = constructor.newInstance();
                //对bean进行注入，使得可以使用@Autowired
                applicationContext.getAutowireCapableBeanFactory().autowireBean(handler);
                //mvc注册
                registerMethod.invoke(requestMappingHandlerMapping,handler);
            }
        }
    }

    @Configuration
    @EnableConfigurationProperties({JpaProperties.class})
    @ConditionalOnClass({ LocalContainerEntityManagerFactoryBean.class, EntityManager.class })
    static class HibernateTenancyAutoConfiguration {

        /**
         * override bean to configure default multi-tenancy
         */
        @Bean
        public JpaProperties jpaProperties(JpaProperties jpaProperties){
            //添加多租户默认配置
            Map<String, String> properties = jpaProperties.getProperties();
            //配置多租户策略
            properties.putIfAbsent(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE.name());
            //配置多租户租户ID解析器
            properties.putIfAbsent(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, CustomMultiTenantIdentifierResolver.class.getName());
            //配置多租户ID链接提供器
            properties.putIfAbsent(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, CustomMultiTenantConnectionProvider.class.getName());

            return jpaProperties;
        }
    }

    @org.springframework.context.annotation.Configuration
    @ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
    @EnableConfigurationProperties(MybatisProperties.class)
    static class MybatisTenancyAutoConfiguration {


        private final MybatisProperties properties;

        private final Interceptor[] interceptors;

        private final ResourceLoader resourceLoader;

        private final DatabaseIdProvider databaseIdProvider;

        private final List<ConfigurationCustomizer> configurationCustomizers;

        public MybatisTenancyAutoConfiguration(MybatisProperties properties,
                                               ObjectProvider<Interceptor[]> interceptorsProvider,
                                               ResourceLoader resourceLoader,
                                               ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                               ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
            this.properties = properties;
            this.interceptors = interceptorsProvider.getIfAvailable();
            this.resourceLoader = resourceLoader;
            this.databaseIdProvider = databaseIdProvider.getIfAvailable();
            this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        }


        /**
         * 在使用liquibase场景情况下，为了避免bean冲突出现需要自定义构造{@link RoutingDataSource}，liquibase构造时，会取唯一的datasource
         * 详细查看@see {@link org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.LiquibaseConfiguration#LiquibaseConfiguration(org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties, org.springframework.boot.autoconfigure.jdbc.DataSourceProperties, org.springframework.core.io.ResourceLoader, org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider)}
         */
        @Bean
        @ConditionalOnMissingBean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public SqlSessionFactory sqlSessionFactory(TenantDataSourceProvider provider) throws Exception {

            RoutingDataSource routingDataSource = new RoutingDataSource(provider);

            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            factory.setDataSource(routingDataSource);
            factory.setVfs(SpringBootVFS.class);
            if (StringUtils.hasText(this.properties.getConfigLocation())) {
                factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
            }
            applyConfiguration(factory);
            if (this.properties.getConfigurationProperties() != null) {
                factory.setConfigurationProperties(this.properties.getConfigurationProperties());
            }
            if (!ObjectUtils.isEmpty(this.interceptors)) {
                factory.setPlugins(this.interceptors);
            }
            if (this.databaseIdProvider != null) {
                factory.setDatabaseIdProvider(this.databaseIdProvider);
            }
            if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
                factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
            }
            if (this.properties.getTypeAliasesSuperType() != null) {
                factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
            }
            if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
                factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
            }
            if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
                factory.setMapperLocations(this.properties.resolveMapperLocations());
            }

            return factory.getObject();
        }

        private void applyConfiguration(SqlSessionFactoryBean factory) {
            org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
            if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
                configuration = new org.apache.ibatis.session.Configuration();
            }
            if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
                for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                    customizer.customize(configuration);
                }
            }
            factory.setConfiguration(configuration);
        }
    }

}
