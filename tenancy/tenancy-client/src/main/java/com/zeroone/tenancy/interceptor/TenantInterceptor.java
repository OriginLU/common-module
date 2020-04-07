package com.zeroone.tenancy.interceptor;

import com.zeroone.tenancy.constants.TenancyConstants;
import com.zeroone.tenancy.miss.handler.TenantCodeMissHandler;
import com.zeroone.tenancy.provider.TenantDataSourceProvider;
import com.zeroone.tenancy.utils.TenantIdentifierHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class TenantInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private final TenantDataSourceProvider provider;

    private final Set<TenantCodeMissHandler> missHandlers;

    public TenantInterceptor(TenantDataSourceProvider provider, Set<TenantCodeMissHandler> tenantCodeMissHandler) {

        this.provider = provider;
        this.missHandlers = tenantCodeMissHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String tenantCode = this.getTenantCode(request);
        log.debug("tenant interceptor set tenant:{}", tenantCode);
        if (StringUtils.isBlank(tenantCode)) {
            log.warn("current tenant code not found : uri -> {}", request.getRequestURI());
            throw new IllegalStateException("tenant code not found");
        }
        if (provider.existsDatasource(tenantCode)) {
            //添加数据源
        }
        // mongo he mysql 都没有配置则报异常
//        if (provider.checkDataSource(tenantCode, null)) {
//            throw new IllegalStateException("tenant code not found");
//        }
        //设置租户信息
        TenantIdentifierHelper.setTenant(tenantCode);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        log.debug("TenantInterceptor removeTenant:{}", TenantIdentifierHelper.getTenant());
        //释放资源
        TenantIdentifierHelper.remove();
    }

    /**
     * 获取租户号
     */
    private String getTenantCode(HttpServletRequest request) {
        String tenantCode = request.getHeader(TenancyConstants.TENANT_CODE);
        if (StringUtils.isBlank(tenantCode) && !CollectionUtils.isEmpty(missHandlers)) {
            return missHandlers.stream().filter(h -> h.match(request)).findFirst().map(h -> h.getTenantCode(request)).orElseGet(() -> null);
        }
        return tenantCode;
    }
}
