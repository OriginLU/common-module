package com.charleslu.tenancy.config;

import com.charleslu.tenancy.interceptor.TenantInterceptor;
import com.charleslu.tenancy.interceptor.TenantMissHandleInterface;
import com.charleslu.tenancy.multi.hibernate.TenantDataSourceProvider;
import com.charleslu.tenancy.service.TenantDataSourceService;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @Author tanglh
 * @Date 2018/11/30 12:06
 */
@Configuration
@EnableAsync
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate", name = {"multiTenancy", "tenant_identifier_resolver", "multi_tenant_connection_provider"})
@Order(-1)
public class TenantConfiguration implements WebMvcConfigurer,ApplicationRunner, ServletContextInitializer {

    private static final Logger log = LoggerFactory.getLogger(TenantConfiguration.class);


    @Value("${liquibase.enable:false}")
    private boolean liquibaseEnable;

    @Autowired
    @Lazy
    private TenantDataSourceService tenantDataSourceService;

    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private LiquibaseProperties liquibaseProperties;

    @Autowired(required = false)
    private Set<TenantMissHandleInterface> tenantMissHandleInterfaces;

    /**
     * 多租户过滤器不过滤url
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
            "/api/common/kafka/**",
            "/app-operation/**",
            "/health/**"
    };


    @Value("${tenancy.interceptor.excludes:}")
    private String userExcludes;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("add TenantInterceptor");
        // 请求拦截
        List<String> allExcludes = new ArrayList<>();
        allExcludes.addAll(Arrays.asList(excludes));
        if (StringUtils.hasText(userExcludes)) {
            log.debug("server has config excludes of {}", userExcludes);
            allExcludes.addAll(Arrays.asList(userExcludes.split(",")));
        }
        registry.addInterceptor(new TenantInterceptor(tenantDataSourceService, tenantMissHandleInterfaces))
                .addPathPatterns("/**").excludePathPatterns(allExcludes.toArray(new String[allExcludes.size()]));
    }


    /**
     * 服务启动后初始化远程配置
     *
     * @param applicationArguments
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.debug("init datasource on Application started");
        tenantDataSourceService.initRemoteDataSourceAsync();
        log.debug("init remote datasource success");
    }

    @Bean("tenantLiquibase")
    public SpringLiquibase tenantLiquibase() {
        SpringLiquibase liquibase = new TenantSpringLiquibase();
        if (null == liquibaseProperties) {
            return liquibase;
        }
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        Boolean shouldRun = liquibaseEnable || liquibaseProperties.isEnabled();
        liquibase.setShouldRun(shouldRun);
        return liquibase;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (null == TenantDataSourceProvider.getTenantDataSource(TenantDataSourceProvider.DEFAULT_KEY)) {
            log.info("init defaultDatasource on Application starting");
            tenantDataSourceProvider.addDefaultDataSource(dataSource);
        } else {
            log.debug("defaultDatasource has init success.");
        }
    }

}