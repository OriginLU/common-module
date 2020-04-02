package com.zeroone.tenancy.hibernate.spi;

import com.google.common.collect.Lists;
import com.zeroone.tenancy.hibernate.constants.MysqlConstants;
import com.zeroone.tenancy.hibernate.model.DataSourceConfigInfo;
import com.zeroone.tenancy.hibernate.utils.TenantIdentifierHelper;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户数据源加载器
 */
@Slf4j
public class TenantDataSourceProvider implements InitializingBean {


    private final Map<String, DataSource> beanMap = new ConcurrentHashMap<>();

    /**
     * 默认的liquibase名称
     */
    private  static final String LIQUIBASE_BEAN_NAME = "liquibase";

    private final Object monitor = new Object();

    /**
     * 是否初始化
     */
    private boolean isInit = false;

    /**
     * 默认字符集
     */
    private String charset;

    /**
     * datasource默认bean名称
     */
    private String beanName;

    /**
     * liquibase配置
     */
    private SpringLiquibase liquibase;

    /**
     * 数据源配置
     */
    private DataSourceProperties dataSourceProperties;

    /**
     * spring上下文
     */
    private DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * 配置bean工厂元数据信息
     */
    private ConfigurationBeanFactoryMetadata configurationBeanFactoryMetadata;


    public TenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        TenantDataSourceContext.setTenantDataSourceContext(this);
    }


    /**
     * 根据传进来的tenantCode决定返回的数据源
     */
    public DataSource get(String tenantCode) {

        if (!StringUtils.hasText(tenantCode)) {
            log.warn("tenant code is empty");
            return null;
        }
        if (beanMap.containsKey(tenantCode)) {
            log.info("get tenant data source:{}", tenantCode);
            return beanMap.get(tenantCode);
        }
        if (beanMap.isEmpty()) {
            log.warn("default data source doesn't init, please wait.");
            return null;
        }
        return null;
    }

    private void addDataSource0(String tenantCode,DataSource dataSource,boolean requireOverride) {

        synchronized (monitor) {
            if (beanMap.containsKey(tenantCode)) {
                if (BooleanUtils.isTrue(requireOverride)) {
                    remove(tenantCode);
                }
                return;
            }
            beanMap.put(tenantCode, dataSource);
        }
    }

    /**
     * 移除对应的数据源
     */
    public void remove(String tenantCode) {

        if (StringUtils.hasText(tenantCode)) {
            return;
        }
        if (beanMap.containsKey(tenantCode) && !TenantIdentifierHelper.DEFAULT.equalsIgnoreCase(tenantCode)) {

            DataSource dataSource = beanMap.get(tenantCode);
            if (dataSource instanceof Closeable){
                try {
                    ((Closeable)dataSource).close();
                } catch (IOException e) {
                    log.error("close data source error:",e);
                }
            }
            beanMap.remove(tenantCode);
        }
    }


    /**
     * 添加数据源
     */
    public void addDataSource(DataSourceConfigInfo config) {

        log.info("add datasource :{} ", config);
        if (null == config) {
            log.warn("remote datasource is empty.");
            return;
        }
        if (BooleanUtils.isNotTrue(config.getRequireOverride()) && null != get(config.getTenantCode())) {
            throw new IllegalStateException("datasource init has error");
        }
        Optional.ofNullable(getDataSource(config)).ifPresent( ds -> addDataSource0(config.getTenantCode(),ds,config.getRequireOverride()));
    }

    /**
     * 初始化数据源
     */
    public DataSource getDataSource(DataSourceConfigInfo config) {

        DataSource dataSource = null;
        log.info("init datasource :{} ", config);
        try {
            try {
                //尝试连接租户指定数据源
                dataSource = createDataSource(config);
                checkConnectionValidity(dataSource);
                initDataBase(dataSource);
            } catch (Exception e) {
                //异常则在默认数据源同实例下创建一个数据库
                log.error("connect to tenant datasource failed:{}", e.getMessage());
                Connection defaultConnection = get(TenantIdentifierHelper.DEFAULT).getConnection();
                createDataBaseIfNecessary(config.getTenantCode(), config.getDatabase(), defaultConnection);
                dataSource = Optional.ofNullable(dataSource).orElseGet(() -> createDataSource(config));
                initDataBase(dataSource);
                if (null != defaultConnection) {
                    defaultConnection.close();
                }
            }
        } catch (SQLException e) {
            log.error("init datasource failed", e);
        }
        return dataSource;
    }

    private void checkConnectionValidity(DataSource dataSource) throws SQLException {

        try (PreparedStatement prepareStatement = getPrepareStatement(dataSource.getConnection(), MysqlConstants.TEST_QUERY)) {
            if (prepareStatement.execute()) {
                throw new IllegalStateException("connection is not available");
            }
        }
    }

    /**
     * 构建数据源
     * 使用spring自带的数据源生成工具@see {@link org.springframework.boot.jdbc.DataSourceBuilder}
     */
    public DataSource createDataSource(DataSourceConfigInfo config) {

        log.info("generate data source:{}", config);
        //1.获取初始化的bean name,通过该bean模板来初始化对应的数据源对象
        if (!isInit){
            synchronized (monitor){
                this.liquibase = (SpringLiquibase) defaultListableBeanFactory.getBean(LIQUIBASE_BEAN_NAME);
                this.dataSourceProperties = defaultListableBeanFactory.getBean(DataSourceProperties.class);
                this.configurationBeanFactoryMetadata = (ConfigurationBeanFactoryMetadata) defaultListableBeanFactory.getBean(ConfigurationBeanFactoryMetadata.BEAN_NAME);
                String[] beanNames = defaultListableBeanFactory.getBeanNamesForType(dataSourceProperties.getType());
                Arrays.stream(beanNames).filter(b -> getAnnotation(defaultListableBeanFactory.getBean(b),b) != null)
                        .findFirst().ifPresent(beanName -> this.beanName = beanName);
                beanMap.put(TenantIdentifierHelper.DEFAULT,(DataSource) defaultListableBeanFactory.getBean(beanName));
                isInit = true;
            }
        }

        //2.创建datasource实例
        DataSource dataSource = DataSourceBuilder.create(dataSourceProperties.getClassLoader())
                .type(dataSourceProperties.getType())
                .driverClassName(dataSourceProperties.determineDriverClassName())
                .url(config.getUrl())
                .password(config.getPassword())
                .username(config.getUsername()).build();


        //3.调用spring自带bean工厂,初始化 bean
        defaultListableBeanFactory.applyBeanPostProcessorsBeforeInitialization(dataSource,beanName);

        return dataSource;
    }

    private  ConfigurationProperties getAnnotation(Object bean, String beanName) {
        ConfigurationProperties annotation = this.configurationBeanFactoryMetadata.findFactoryAnnotation(beanName,  ConfigurationProperties.class);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(bean.getClass(),  ConfigurationProperties.class);
        }
        return annotation;
    }
    /**
     * 检查数据库是否存在，不存在则创建数据库
     */
    private void createDataBaseIfNecessary(String tenantIdentifier, String databaseName, Connection connection) {

        if (TenantIdentifierHelper.DEFAULT.equals(tenantIdentifier)) {
            return;
        }
        if (!StringUtils.hasText(databaseName)) {
            log.error("cannot get database name");
            return;
        }
        List<PreparedStatement> psSet = Lists.newArrayList();
        try {
            log.info("database name:{}", databaseName);
            //1.check whether database exist，if not exist will be create
            PreparedStatement ps = getPrepareStatement(connection, MysqlConstants.QUERY_SCHEMA_SQL, databaseName);
            psSet.add(ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                log.info("database:{} is not exist, system will be auto create:{}", databaseName, databaseName);

                if (StringUtils.isEmpty(charset)) {
                    charset = MysqlConstants.DEFAULT_CHARSET;
                    log.debug("charset:{}", charset);
                }
                //create database
                PreparedStatement createStatement = getPrepareStatement(connection, MysqlConstants.CREATE_DATABASE_SQL, databaseName, charset);
                psSet.add(createStatement);
                createStatement.execute();
            }
            rs.close();
            PreparedStatement useStatement = getPrepareStatement(connection, MysqlConstants.USE_DATABASE_SQL, databaseName);
            useStatement.execute();
            psSet.add(useStatement);
        } catch (Exception e) {
            log.error("connect to database:{} failed, connection:{}, exception:{}", databaseName, connection, e);
        } finally {
            if (ObjectUtils.isEmpty(psSet)){
                psSet.forEach(JdbcUtils::closeStatement);
            }
        }
    }

    private PreparedStatement getPrepareStatement(Connection connection, String sql, Object... args) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        if (ObjectUtils.isEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i, args[i]);
            }
        }
        return ps;
    }


    /**
     * 使用liquibase初始化数据库
     */
    private void initDataBase(DataSource dataSource) {

        log.debug("current datasource:{}", dataSource);
        try {
            log.info("start init database by liquibase");
            liquibase.setDataSource(dataSource);
            liquibase.afterPropertiesSet();
            log.info("success init database by liquibase");
        } catch (Exception e) {
            log.error("init database failed, {}", dataSource, e);
        }
    }

    /**
     * 用于初始化多租户系统
     */
    @Override
    public void afterPropertiesSet(){

    }
}