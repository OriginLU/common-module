package com.zeroone.tenancy.hibernate.service;

import com.google.common.collect.Lists;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.hibernate.entity.TenantDataSourceInfo;
import com.zeroone.tenancy.hibernate.repository.TenantDataSourceInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        if (null == tenantDataSourceInfo.getRequireOverride()) {
            tenantDataSourceInfo.setRequireOverride(false);
        }

        tenantDataSourceInfoRepository.save(tenantDataSourceInfo);
    }



    public List<DataSourceInfo> findTenantDataSourceInfoByServerNameAndState(String serverName,Integer state){


        Optional<List<TenantDataSourceInfo>> optionalInfos = tenantDataSourceInfoRepository.findByServerNameAndState(serverName, state);

        List<DataSourceInfo> dataSourceInfos = Lists.newArrayList();

        optionalInfos.ifPresent(ds -> {
            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            BeanUtils.copyProperties(ds,dataSourceInfo);
            dataSourceInfos.add(dataSourceInfo);
        });

        return dataSourceInfos;
    }


}
