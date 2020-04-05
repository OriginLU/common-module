package com.zeroone.tenancy.hibernate.rest;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.enums.DataSourceStateEnum;
import com.zeroone.tenancy.hibernate.service.TenantDataSourceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zero-one.lu
 * @since 2020-04-05
 */
@RestController
@ConditionalOnBean(TenantDataSourceInfoService.class)
public class TenantDataSourceConfigResource extends ExceptionHandlerResource {


    @Autowired
    private TenantDataSourceInfoService tenantDataSourceInfoService;



    @PostMapping("save")
    public ResponseEntity<String> save(@Validated DataSourceInfo dataSourceInfo){

        tenantDataSourceInfoService.saveTenantDataSoureInfo(dataSourceInfo);

        return ResponseEntity.ok("success");
    }


    @GetMapping("/datasource/config/{serverName}")
    public ResponseEntity<List<DataSourceInfo>> getActiveDataSourceInfo(@PathVariable("serverName") String serverName){

        return ResponseEntity.ok(tenantDataSourceInfoService.findTenantDataSourceInfoByServerNameAndState(serverName, DataSourceStateEnum.ACTIVE.getCode()));
    }


    @GetMapping("/datasource/config/{serverName}/{state}")
    public ResponseEntity<List<DataSourceInfo>> getDataSourceInfoBySererNameAndState(@PathVariable("serverName")String serverName,@PathVariable("state") Integer state){

        return ResponseEntity.ok(tenantDataSourceInfoService.findTenantDataSourceInfoByServerNameAndState(serverName, state));
    }
}
