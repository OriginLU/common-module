package com.zeroone.tenancy.service;

import com.google.common.collect.Lists;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.enums.DataSourceConfigStatusEnum;
import com.zeroone.tenancy.enums.DatasourceStatusEnum;
import com.zeroone.tenancy.repository.TenantDataSourceInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zero-one.lu
 * @since 2020-04-05
 */
public class TenantDataSourceInfoService {


    @Autowired
    private TenantDataSourceInfoRepository tenantDataSourceInfoRepository;




    public void saveTenantDataSoureInfo(DataSourceInfo dataSourceInfo){

        TenantDataSourceInfo tenantDataSourceInfo = new TenantDataSourceInfo();

        BeanUtils.copyProperties(dataSourceInfo,tenantDataSourceInfo);

        tenantDataSourceInfo.setCreateTime(new Date());
        tenantDataSourceInfo.setModifyTime(new Date());

        tenantDataSourceInfoRepository.save(tenantDataSourceInfo);
    }




    public List<DataSourceInfo> getActiveDataSourceInfo(String tenantCode) {

        List<TenantDataSourceInfo> tenantDataSourceInfos = tenantDataSourceInfoRepository.findByStateAndTenantCode(DataSourceConfigStatusEnum.ENABLE, tenantCode);

        if (CollectionUtils.isEmpty(tenantDataSourceInfos)){
            return Collections.emptyList();
        }
        List<DataSourceInfo> list = new ArrayList<>();
        tenantDataSourceInfos.forEach(tenantDataSourceInfo -> {

            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            BeanUtils.copyProperties(tenantDataSourceInfo,dataSourceInfo);
            list.add(dataSourceInfo);
        });

        return list;
    }

    public DataSourceInfo getSpecifiedActiveDataSourceInfo(String tenantCode, String serverName, String databaseType) {

        tenantDataSourceInfoRepository.findByTenantCodeAndServerNameAndType(tenantCode,serverName,databaseType);

        return null;
    }
}
