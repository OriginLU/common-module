package com.charleslu.tenancy.multi.hibernate;

import com.charleslu.tenancy.multi.util.ThreadTenantUtil;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.Optional;

/**
 * 多租户解析器
 *
 * @Author tanglh
 * @Date 2018/11/23 12:45
 */
public class   MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    /**
     * 从获取当前线程租户信息
     *
     * @return 数据源名称
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        return Optional.ofNullable(ThreadTenantUtil.getTenant()).orElse(TenantDataSourceProvider.DEFAULT_KEY);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
