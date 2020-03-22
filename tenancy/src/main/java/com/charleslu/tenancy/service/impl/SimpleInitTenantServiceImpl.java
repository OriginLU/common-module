package com.charleslu.tenancy.service.impl;

import com.charleslu.common.constants.Constants;
import com.charleslu.tenancy.entity.DataSourceInfo;
import com.charleslu.tenancy.multi.mongo.TenantMongoDatabaseProvider;
import com.charleslu.tenancy.service.InitTenantService;
import com.charleslu.tenancy.service.TenantDataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


/**
 * @Author tanglh
 * @Date 2018/12/21 16:22
 */
@Service
@Order(0)
public class SimpleInitTenantServiceImpl implements InitTenantService {

    private static final Logger log = LoggerFactory.getLogger(SimpleInitTenantServiceImpl.class);

    @Autowired
    private TenantDataSourceService tenantDataSourceService;


    @Override
    public boolean initTenantInfo(DataSourceInfo dataSourceInfo) {
        log.info("simple init tenantInfo:{}", dataSourceInfo);
        // 使用springLiquibase初始化数据库

        tenantDataSourceService.initDatabase(dataSourceInfo);
        tenantDataSourceService.addRemoteDataSource(dataSourceInfo);

        return true;
    }
}