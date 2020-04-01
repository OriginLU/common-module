package com.zeroone.tenancy.hibernate.service;

import com.zeroone.tenancy.hibernate.constants.MysqlConstants;
import com.zeroone.tenancy.hibernate.model.DataSourceConfigInfo;
import com.zeroone.tenancy.hibernate.spi.TenantDataSourceProvider;
import com.zeroone.tenancy.hibernate.utils.TenantIdentifierHelper;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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
                checkConnectionValidity(dataSource);
                initDataBase(dataSource);
            } catch (Exception e) {
                //异常则在默认数据源同实例下创建一个数据库
                log.error("connect to tenant database failed:{}", e.getMessage());
                final Connection defaultConnection = TenantDataSourceProvider.get(TenantIdentifierHelper.DEFAULT).getConnection();
                createDataBaseIfNecessary(config.getTenantCode(), config.getDatabase(), defaultConnection);
                dataSource = Optional.ofNullable(dataSource).orElseGet(() -> tenantDataSourceProvider.createDataSource(config));
                initDataBase(dataSource);
                if (null != defaultConnection) {
                    defaultConnection.close();
                }
            }
        } catch (SQLException e) {
            log.error("init database failed", e);
        }
    }

    private void checkConnectionValidity(DataSource dataSource) throws SQLException {

        try (ResultSet rs = getPrepareStatement(dataSource.getConnection(), MysqlConstants.TEST_QUERY).executeQuery()) {
            if (!(rs.next() && rs.getInt(1) > 0)){
                throw new IllegalStateException("connection is not available");
            }
        }
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
        try {
            log.info("database name:{}", databaseName);
            //1.check whether database exist，if not exist will be create
            try(ResultSet rs = getPrepareStatement(connection, MysqlConstants.QUERY_SCHEMA_SQL, databaseName).executeQuery()){
                if (rs.next() && rs.getInt(1) == 0) {
                    log.info("database:{} is not exist, system will be auto create:{}", databaseName, databaseName);

                    if (StringUtils.isEmpty(charset)) {
                        charset = MysqlConstants.DEFAULT_CHARSET;
                        log.debug("charset:{}", charset);
                    }
                    //create database
                    PreparedStatement createStatement = getPrepareStatement(connection, MysqlConstants.CREATE_DATABASE_SQL, databaseName, charset);
                    log.info("create database sql:{}", createStatement.toString());
                    createStatement.execute();
                }
                getPrepareStatement(connection, MysqlConstants.USE_DATABASE_SQL, databaseName).execute();
            }
        } catch (Exception e) {
            log.error("connect to database:{} failed, connection:{}, exception:{}", databaseName, connection, e);
        }
    }

    private PreparedStatement getPrepareStatement(Connection connection,String sql,Object ...args) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        if (ObjectUtils.isEmpty(args)){
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i,args[i]);
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
}
