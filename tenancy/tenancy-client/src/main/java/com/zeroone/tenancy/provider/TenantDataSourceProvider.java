package com.zeroone.tenancy.provider;

import com.google.common.base.Throwables;
import com.zeroone.tenancy.constants.MysqlConstants;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.event.DatasourceEventPublisher;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户数据源加载器
 */
public class TenantDataSourceProvider {


    private final static Logger log = LoggerFactory.getLogger(TenantDataSourceProvider.class);


    private final Map<String, DataSourceInfo> dataSourceInfoMap = new ConcurrentHashMap<>();

    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

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
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * 配置bean工厂元数据信息
     */
    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;


    private DatasourceEventPublisher eventPublisher;


    public TenantDataSourceProvider(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        initialProperties();
        TenantDataSourceContext.setTenantDataSourceContext(this);
    }

    private void initialProperties() {

        //1.获取liquibase bean
        this.liquibase = (SpringLiquibase) defaultListableBeanFactory.getBean(LIQUIBASE_BEAN_NAME);
        //2.获取bean配置
        this.dataSourceProperties = defaultListableBeanFactory.getBean(DataSourceProperties.class);
        //3.获取beanFactoryMeta
        this.beanFactoryMetadata = (ConfigurationBeanFactoryMetadata) defaultListableBeanFactory.getBean(ConfigurationBeanFactoryMetadata.BEAN_NAME);
        //设置监控
        this.eventPublisher = defaultListableBeanFactory.getBean(DatasourceEventPublisher.class);
        //4.获取初始化bean名称
        String[] beanNames = defaultListableBeanFactory.getBeanNamesForType(dataSourceProperties.getType());
        //5.获取数据源配置工厂bean的名称，为后续初始化做准备
        Arrays.stream(beanNames).filter(b -> getAnnotation(defaultListableBeanFactory.getBean(b), b) != null)
                .findFirst().ifPresent(beanName -> this.beanName = beanName);
        //6.添加默认数据源
        dataSourceMap.put(TenantIdentifierHelper.DEFAULT, (DataSource) defaultListableBeanFactory.getBean(beanName));

    }

    public DataSourceInfo getDatasourceInfo(String tenantCode){
        return dataSourceInfoMap.get(tenantCode);
    }

    /**
     * 根据传进来的tenantCode决定返回的数据源
     */
    public DataSource getDataSource(String tenantCode) {

        if (!StringUtils.hasText(tenantCode)) {
            log.warn("tenant code is empty");
            return null;
        }
        if (dataSourceMap.containsKey(tenantCode)) {
            log.info("get tenant data source:{}", tenantCode);
            eventPublisher.publishRunningEvent(this,tenantCode);
            return dataSourceMap.get(tenantCode);
        }

        if (dataSourceInfoMap.containsKey(tenantCode)) {

            synchronized (monitor) {
                if (dataSourceMap.containsKey(tenantCode)) {
                    eventPublisher.publishRunningEvent(this,tenantCode);
                    return dataSourceMap.get(tenantCode);
                }
                DataSourceInfo dataSourceInfo = dataSourceInfoMap.get(tenantCode);
                try {
                    //1.创建数据源
                    DataSource dataSource = createDataSource(dataSourceInfo);
                    //2.检查数据源的有效性
                    checkConnectionValidity(dataSource);
                    //3.设置数据源缓存
                    dataSourceMap.put(tenantCode, dataSource);
                    //4.设置监控指标
                    eventPublisher.publishRunningEvent(this,tenantCode);

                    return dataSource;
                } catch (SQLException e) {
                    throw new IllegalStateException("create data source occurred error", e);
                }
            }
        }

        if (dataSourceMap.isEmpty()) {
            log.warn("default data source doesn't init, please wait.");
            return null;
        }
        return null;
    }


    public Map<String, DataSource> getDataSourceMap() {
        return this.dataSourceMap;
    }

    private void overrideDataSource(String tenantCode, DataSource dataSource, boolean requireOverride) {

        synchronized (monitor) {
            if (dataSourceMap.containsKey(tenantCode) && BooleanUtils.isTrue(requireOverride)) {
                remove(tenantCode);
            }
            dataSourceMap.put(tenantCode, dataSource);
        }
    }

    /**
     * 移除对应的数据源
     */
    public void remove(String tenantCode) {

        if (!StringUtils.hasText(tenantCode)) {
            return;
        }
        if (dataSourceMap.containsKey(tenantCode) && !TenantIdentifierHelper.DEFAULT.equalsIgnoreCase(tenantCode)) {

            DataSource dataSource = dataSourceMap.get(tenantCode);
            if (dataSource instanceof Closeable) {
                try {
                    ((Closeable) dataSource).close();
                } catch (IOException e) {
                    log.error("close data source error:", e);
                }
            }
            dataSourceMap.remove(tenantCode);
            eventPublisher.publishRemoveEvent(this,tenantCode);
        }
    }


    /**
     * 添加数据源
     */
    public synchronized void addDataSource(DataSourceInfo config) {

        log.info("add datasource :{} ", config);
        if (null == config) {
            log.warn("remote datasource is empty.");
            return;
        }
        //判断是否需要重写
        String tenantCode = config.getTenantCode();
        if (BooleanUtils.isTrue(config.getRequireOverride())
                && dataSourceMap.containsKey(tenantCode)
                && dataSourceInfoMap.containsKey(tenantCode)) {
            DataSource dataSource = createDataSource(config);
            //重写数据源
            overrideDataSource(tenantCode,dataSource,config.getRequireOverride());
            dataSourceInfoMap.put(tenantCode,config);
            eventPublisher.publishOverrideEvent(this,tenantCode);

            return;
        }

        prepareDataSourceInfo(Collections.singletonList(config));
        try {
            //1.创建数据源
            DataSource dataSource = createDataSource(config);
            //2.检查数据源的有效性
            checkConnectionValidity(dataSource);
            //3.设置数据源缓存
            dataSourceMap.put(tenantCode, dataSource);
        } catch (SQLException e) {
            throw  new IllegalStateException(e);
        }
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
    public boolean hasDatasource(String tenantCode) {
        return dataSourceMap.get(tenantCode) != null;
    }

    /**
     * 准备数据源
     * 1.检查是否存在数据源
     * 2.创建数据源
     */
    public void prepareDataSourceInfo(List<DataSourceInfo> dataSourceInfos) {

        if (CollectionUtils.isEmpty(dataSourceInfos)) {
            return;
        }

        DbUtils.loadDriver(dataSourceProperties.getDriverClassName());
        try {

            QueryRunner queryRunner = new QueryRunner();
            for (DataSourceInfo dataSourceInfo : dataSourceInfos) {

                Connection connection = DriverManager.getConnection(dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword());

                if (dataSourceInfo.getIsEnable() != null && !dataSourceInfo.getIsEnable()) {
                    continue;
                }
                //判断数据是否存在
                boolean isAbsent = queryRunner.query(connection,
                        MysqlConstants.QUERY_SCHEMA_SQL,
                        rs ->  rs.next() && rs.getInt(1) == 0,
                        dataSourceInfo.getDatabase());

                if (isAbsent) {
                    //1.创建数据库
                    queryRunner.execute(connection, MysqlConstants.CREATE_DATABASE_SQL, dataSourceInfo.getDatabase(), MysqlConstants.DEFAULT_CHARSET);
                    //2.设置数据库
                    queryRunner.execute(connection, MysqlConstants.USE_DATABASE_SQL, dataSourceInfo.getDatabase());
                }
                //liquibase初始化数据表
                initializeDataBase(connection);
                dataSourceInfoMap.put(dataSourceInfo.getTenantCode(), dataSourceInfo);
                eventPublisher.publishInitEvent(this,dataSourceInfo.getTenantCode());
            }

        } catch (Exception e) {
            log.error("init database error", e);
            throw new IllegalStateException(e);

        }


    }


    private void checkConnectionValidity(DataSource dataSource) throws SQLException {

        Connection connection = DataSourceUtils.doGetConnection(dataSource);

        try {
             new QueryRunner().execute(connection, MysqlConstants.TEST_QUERY);
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


    private void initializeDataBase(Connection connection) {

        try {
            log.info("start init database by liquibase");
            liquibase.setDataSource(wrap(connection));
            liquibase.afterPropertiesSet();
            log.info("success init database by liquibase");
        } catch (Exception e) {
            log.error("init database failed, {}", connection, e);
        }
    }

    private DataSource wrap(Connection connection) {

        return new DataSource() {
            @Override
            public Connection getConnection() {
                return connection;
            }

            @Override
            public Connection getConnection(String username, String password) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T unwrap(Class<T> iface) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isWrapperFor(Class<?> iface)  {
                throw new UnsupportedOperationException();
            }

            @Override
            public PrintWriter getLogWriter() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setLogWriter(PrintWriter out) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setLoginTimeout(int seconds) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getLoginTimeout() {
                throw new UnsupportedOperationException();
            }

            @Override
            public java.util.logging.Logger getParentLogger() {
                throw new UnsupportedOperationException();
            }
        };
    }


}
