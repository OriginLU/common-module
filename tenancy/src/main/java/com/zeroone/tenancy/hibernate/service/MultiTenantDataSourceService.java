package com.zeroone.tenancy.hibernate.service;

import com.zeroone.tenancy.hibernate.constants.MysqlConstants;
import com.zeroone.tenancy.hibernate.model.DataSourceConfigInfo;
import com.zeroone.tenancy.hibernate.spi.TenantDataSourceProvider;
import com.zeroone.tenancy.hibernate.utils.TenantIdentifierHelper;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MultiTenantDataSourceService {


    private String charset;

    private SpringLiquibase liquibase;

    private TenantDataSourceProvider tenantDataSourceProvider;


    public MultiTenantDataSourceService(SpringLiquibase liquibase, TenantDataSourceProvider tenantDataSourceProvider) {
        this.liquibase = liquibase;
        this.tenantDataSourceProvider = tenantDataSourceProvider;
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
        if (BooleanUtils.isNotTrue(config.getRequireOverride()) && null != TenantDataSourceProvider.get(config.getTenantCode())) {
            throw new IllegalStateException("datasource init has error");
        }
        initDataSource(config);

    }

    /**
     * 初始化数据源
     */
    public void initDataSource(DataSourceConfigInfo config) {

        log.info("init multi database :{} ", config);
        try {
            DataSource dataSource = null;
            try {
                //尝试连接租户指定数据源
                dataSource = tenantDataSourceProvider.createDataSource(config);
                dataSource.getConnection();
                initDataBase(dataSource);
            } catch (Exception e) {
                //异常则在默认数据源同实例下创建一个数据库
                log.error("connect to tenant database failed:{}", e.getMessage());
                final Connection defaultConnection = TenantDataSourceProvider.get(TenantIdentifierHelper.DEFAULT).getConnection();
                createDataBaseIfNecessary(config.getTenantCode(), config.getDatabase(), defaultConnection);
                if (null == dataSource) {
                    dataSource = tenantDataSourceProvider.createDataSource(config);
                }
                initDataBase(dataSource);
                if (null != defaultConnection) {
                    defaultConnection.close();
                }
            }
        } catch (SQLException e) {
            log.error("init database failed", e);
        }
    }

    /**
     * 使用租户数据库
     * 如果数据库不存在则在默认
     */
    private void createDataBaseIfNecessary(String tenantIdentifier, String databaseName, Connection connection) {

        if (TenantIdentifierHelper.DEFAULT.equals(tenantIdentifier)) {
            return;
        }
        if (!StringUtils.hasText(databaseName)) {
            log.error("cannot get database name");
            return;
        }
        try {
            log.info("database name:{}", databaseName);
            PreparedStatement ps = connection.prepareStatement(MysqlConstants.QUERY_SCHEMA_SQL);
            ps.setString(1, databaseName);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                log.info("database:{} is not exist, system will be auto create:{}", databaseName, databaseName);

                if (StringUtils.isEmpty(charset)) {
                    charset = MysqlConstants.DEFAULT_CHARSET;
                    log.debug("charset:{}", charset);
                }
                String createSQL = String.format(MysqlConstants.CREATE_DATABASE_SQL, databaseName, charset);
                log.info("create database sql:{}", createSQL);
                connection.createStatement().execute(createSQL);
            }
            connection.createStatement().execute(String.format(MysqlConstants.USE_DATABASE_SQL, databaseName));
        } catch (Exception e) {
            log.error("connect to database:{} failed, connection:{}, exception:{}", databaseName, connection, e);
        }
    }


    /**
     * 使用liquibase初始化数据库
     */
    private void initDataBase(DataSource dataSource) {

        log.debug("current datasource:{}", dataSource);
        try {
            log.info("start init multi by liquibase");
            liquibase.setDataSource(dataSource);
            liquibase.afterPropertiesSet();
            log.info("success init multi by liquibase");
        } catch (Exception e) {
            log.error("springLiquibase init multi failed, {}", dataSource, e);
        }
    }
}
