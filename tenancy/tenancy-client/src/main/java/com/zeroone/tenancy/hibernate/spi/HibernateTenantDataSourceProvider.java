package com.zeroone.tenancy.hibernate.spi;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.zeroone.tenancy.hibernate.constants.MysqlConstants;
import com.zeroone.tenancy.dto.DataSourceInfo;
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
import org.springframework.jdbc.datasource.DataSourceUtils;
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
public class HibernateTenantDataSourceProvider implements InitializingBean {


    private final Map<String, DataSource> beanMap = new ConcurrentHashMap<>();

    /**
     * 默认的liquibase名称
     */
    private static final String LIQUIBASE_BEAN_NAME = "liquibase";

    /**
     * monitor lock
     */
    private final Object monitor = new Object();

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
    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;


    public HibernateTenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        HibernateTenantDataSourceContext.setHibernateTenantDataSourceContext(this);
    }


    /**
     * 根据传进来的tenantCode决定返回的数据源
     */
    public DataSource getDataSource(String tenantCode) {

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

    private void addDataSource0(String tenantCode, DataSource dataSource, boolean requireOverride) {

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
            if (dataSource instanceof Closeable) {
                try {
                    ((Closeable) dataSource).close();
                } catch (IOException e) {
                    log.error("close data source error:", e);
                }
            }
            beanMap.remove(tenantCode);
        }
    }


    /**
     * 添加数据源
     */
    public void addDataSource(DataSourceInfo config) {

        log.info("add datasource :{} ", config);
        if (null == config) {
            log.warn("remote datasource is empty.");
            return;
        }
        if (BooleanUtils.isNotTrue(config.getRequireOverride()) && null != getDataSource(config.getTenantCode())) {
            throw new IllegalStateException("datasource init has error");
        }
        Optional.ofNullable(offerDataSource(config)).ifPresent(ds -> addDataSource0(config.getTenantCode(), ds, config.getRequireOverride()));
    }

    /**
     * 检查数据源有效性
     */
    public boolean checkDatasource(String tenantCode) {

        log.info("check datasource :{}", tenantCode);
        DataSource dataSource = getDataSource(tenantCode);
        try {
            if (dataSource == null) {
                return false;
            }
            checkConnectionValidity(dataSource);
            return true;
        } catch (Exception e) {
            log.error("tenant[{}] datasource happen error:{}", tenantCode, Throwables.getRootCause(e).getMessage());
            return false;
        }
    }


    /**
     * 检查数据源有效性
     */
    public boolean existsDatasource(String tenantCode) {

        return getDataSource(tenantCode) != null;
    }

    /**
     * 获取数据源
     */
    public DataSource offerDataSource(DataSourceInfo config) {

        DataSource dataSource = null;
        log.info("init datasource :{} ", config);
        try {
            try {
                //1.创建数据源
                dataSource = createDataSource(config);
                //2.检查数据源的有效性
                checkConnectionValidity(dataSource);
                //3.使用liquibase进行数据源初始化
                initializeDataBase(dataSource);
            } catch (Exception e) {
                log.error("connect to tenant datasource failed:{}", e.getMessage());
                Connection defaultConnection = getDataSource(TenantIdentifierHelper.DEFAULT).getConnection();
                //检查数据库是否存在，不存在进行数据库创建
                createDataBaseIfNecessary(config.getTenantCode(), config.getDatabase(), defaultConnection);
                //创建数据源
                dataSource = Optional.ofNullable(dataSource).orElseGet(() -> createDataSource(config));
                //初始化数据库
                initializeDataBase(dataSource);
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

        Connection connection = DataSourceUtils.doGetConnection(dataSource);
        try (PreparedStatement prepareStatement = getPrepareStatement(connection, MysqlConstants.TEST_QUERY)) {
            prepareStatement.execute();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    /**
     * 构建数据源
     * 使用spring自带的数据源生成工具@see {@link org.springframework.boot.jdbc.DataSourceBuilder}
     */
    public DataSource createDataSource(DataSourceInfo config) {

        log.info("generate data source:{}", config);
        //1.创建datasource实例
        DataSource dataSource = DataSourceBuilder.create(dataSourceProperties.getClassLoader())
                .type(dataSourceProperties.getType())
                .driverClassName(dataSourceProperties.determineDriverClassName())
                .url(config.getUrl())
                .password(config.getPassword())
                .username(config.getUsername()).build();


        //2.调用spring自带bean工厂,初始化 bean
        defaultListableBeanFactory.applyBeanPostProcessorsBeforeInitialization(dataSource, beanName);

        return dataSource;
    }

    private ConfigurationProperties getAnnotation(Object bean, String beanName) {
        ConfigurationProperties annotation = this.beanFactoryMetadata.findFactoryAnnotation(beanName, ConfigurationProperties.class);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class);
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
            if (ObjectUtils.isEmpty(psSet)) {
                psSet.forEach(JdbcUtils::closeStatement);
            }
        }
    }

    private PreparedStatement getPrepareStatement(Connection connection, String sql, Object... args) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        if (!ObjectUtils.isEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
        }
        return ps;
    }


    /**
     * 使用liquibase初始化数据库
     */
    private void initializeDataBase(DataSource dataSource) {

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
    public void afterPropertiesSet() {

        //1.执行bean后续的初始化工作
        postInitProperties();

    }

    private void postInitProperties() {

        //1.获取liquibase bean
        this.liquibase = (SpringLiquibase) defaultListableBeanFactory.getBean(LIQUIBASE_BEAN_NAME);
        //2.获取bean配置
        this.dataSourceProperties = defaultListableBeanFactory.getBean(DataSourceProperties.class);
        //3.获取beanFactoryMeta
        this.beanFactoryMetadata = (ConfigurationBeanFactoryMetadata) defaultListableBeanFactory.getBean(ConfigurationBeanFactoryMetadata.BEAN_NAME);
        //4.获取初始化bean名称
        String[] beanNames = defaultListableBeanFactory.getBeanNamesForType(dataSourceProperties.getType());
        //5.获取数据源配置工厂bean的名称，为后续初始化做准备
        Arrays.stream(beanNames).filter(b -> getAnnotation(defaultListableBeanFactory.getBean(b), b) != null)
                .findFirst().ifPresent(beanName -> this.beanName = beanName);
        //6.添加默认数据源
        beanMap.put(TenantIdentifierHelper.DEFAULT, (DataSource) defaultListableBeanFactory.getBean(beanName));

    }

}
