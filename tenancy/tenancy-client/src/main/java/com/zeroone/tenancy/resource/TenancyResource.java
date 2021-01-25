package com.zeroone.tenancy.resource;

import com.zeroone.tenancy.annotation.TenancyApi;
import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@TenancyApi
@RestController
@RequestMapping("/tenancy")
public class TenancyResource {

    @Autowired
    private TenantDataSourceProvider provider;


    /**
     * 添加租户数据源
     */
    @PostMapping("add-datasource")
    public void addDatasource(DataSourceInfo dataSourceInfo){
        provider.addDataSource(dataSourceInfo);
    }


    /**
     * 移除对应租户数据源
     */
    @GetMapping("remove-datasource/{tenantCode}")
    public void removeDatasource(@PathVariable("tenantCode") String tenantCode){
        provider.remove(tenantCode);
    }
}
