package com.zeroone.tenancy.route;

import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Optional;

public class RoutingDataSource extends AbstractRoutingDataSource {


    private final TenantDataSourceProvider tenantDataSourceProvider;


    public RoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
        this.setDefaultTargetDataSource(tenantDataSourceProvider.getDataSource(TenantIdentifierHelper.DEFAULT));
    }


    @Override
    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
       throw new IllegalArgumentException("unsupport this operation");
    }

    @Override
    protected DataSource determineTargetDataSource() {
        return tenantDataSourceProvider.getDataSource(TenantIdentifierHelper.getTenant());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return Optional.ofNullable(TenantIdentifierHelper.getTenant()).orElse(TenantIdentifierHelper.DEFAULT);
    }
}
