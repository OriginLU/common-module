package com.zeroone.tenancy.rest;

import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.service.TenantDataSourceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zero-one.lu
 * @since 2020-04-05
 */
@RestController
@RequestMapping("/api/data-source/config")
public class TenantDataSourceConfigResource extends ExceptionHandlerResource {


    @Autowired
    private TenantDataSourceInfoService tenantDataSourceInfoService;


    @PostMapping("tenant/server/{tenantCode}")
    public List<DataSourceInfo> getActiveDataSourceInfo(@PathVariable("tenantCode") String tenantCode){
        return tenantDataSourceInfoService.getActiveDataSourceInfo(tenantCode);
    }

    @PostMapping("save")
    public ResponseEntity<String> save(@Validated DataSourceInfo dataSourceInfo){

        tenantDataSourceInfoService.saveTenantDataSoureInfo(dataSourceInfo);
        return ResponseEntity.ok("success");
    }


}
