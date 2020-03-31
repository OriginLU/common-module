package com.zeroone.tenancy.hibernate.spi;

import com.zeroone.tenancy.hibernate.model.DataSourceConfigInfo;
import com.zeroone.tenancy.hibernate.utils.TenantIdentifierHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户数据源加载器
 */
@Slf4j
public class TenantDataSourceProvider {


    private static final Map<String, DataSource> DATA_SOURCE_CONTEXT = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    private String beanName;

    private DataSourceProperties dataSourceProperties;

    private DefaultListableBeanFactory defaultListableBeanFactory;


    public TenantDataSourceProvider(DataSourceProperties dataSourceProperties,DefaultListableBeanFactory defaultListableBeanFactory) {

        this.beanName = "tenant_provider";
        this.dataSourceProperties = dataSourceProperties;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }


    /**
     * 根据传进来的tenantCode决定返回的数据源
     */
    public static DataSource get(String tenantCode) {

        if (!StringUtils.hasText(tenantCode)) {
            log.warn("tenant code is empty");
            return null;
        }
        if (DATA_SOURCE_CONTEXT.containsKey(tenantCode)) {
            log.info("get tenant data source:{}", tenantCode);
            return DATA_SOURCE_CONTEXT.get(tenantCode);
        }
        if (DATA_SOURCE_CONTEXT.isEmpty()) {
            log.warn("default data source doesn't init, please wait.");
            return null;
        }
        return null;
    }

    public void add(DataSourceConfigInfo config) {
        log.debug("add data source:{}", config);
        if (null == config) {
            log.warn("data source configuration information is empty");
            return;
        }
        synchronized (config.getTenantCode()) {
            if (DATA_SOURCE_CONTEXT.containsKey(config.getTenantCode())) {
                if (BooleanUtils.isTrue(config.getRequireOverride())) {
                    remove(config.getTenantCode());
                }
                return;
            }
            DataSource hikariDataSource = createDataSource(config);
            DATA_SOURCE_CONTEXT.put(config.getTenantCode(), hikariDataSource);
        }
    }

    /**
     * 构建数据源
     * 使用spring自带的数据源生成工具@see {@link org.springframework.boot.jdbc.DataSourceBuilder}
     */
    public DataSource createDataSource(DataSourceConfigInfo config) {
        log.info("generate data source:{}", config);

        //1.创建datasource实例
        DataSource dataSource = DataSourceBuilder.create(dataSourceProperties.getClassLoader())
                .type(dataSourceProperties.getType())
                .driverClassName(dataSourceProperties.determineDriverClassName())
                .url(config.getUrl())
                .password(config.getPassword())
                .username(config.getUsername()).build();


        //2.获取初始化的bean name,通过该bean模板来初始化对应的数据源对象
        if (!StringUtils.hasText(beanName)){
            synchronized (monitor){
                String[] beanNames = defaultListableBeanFactory.getBeanNamesForType(dataSourceProperties.getType());
                for (String beanName : beanNames) {
                    Object bean = defaultListableBeanFactory.getBean(beanName);
                    if (!AopUtils.isAopProxy(bean)) {
                        this.beanName = beanName;
                    }
                }
            }
        }
        //3.调用spring自带bean工厂,初始化 bean
        defaultListableBeanFactory.initializeBean(dataSource,beanName);

        return dataSource;
    }

    /**
     * 移除对应的数据源
     */
    public void remove(String tenantCode) {

        if (StringUtils.hasText(tenantCode)) {
            return;
        }
        if (DATA_SOURCE_CONTEXT.containsKey(tenantCode) && !TenantIdentifierHelper.DEFAULT.equalsIgnoreCase(tenantCode)) {

            DataSource dataSource = DATA_SOURCE_CONTEXT.get(tenantCode);
            if (dataSource instanceof Closeable){
                try {
                    ((Closeable)dataSource).close();
                } catch (IOException e) {
                    log.error("close data source error:",e);
                }
            }
            DATA_SOURCE_CONTEXT.remove(tenantCode);
        }
    }
}
