package com.zeroone.tenancy.hibernate.interceptor;

import com.hazelcast.util.CollectionUtil;
import com.zeroone.tenancy.hibernate.service.MultiTenantDataSourceService;
import com.zeroone.tenancy.hibernate.utils.TenantIdentifierHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class TenantInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private final MultiTenantDataSourceService tenantDataSourceService;

//    private final Set<TenantMissHandleInterface> tenantMissHandleInterfaces;

    public TenantInterceptor(MultiTenantDataSourceService tenantDataSourceService) {
        this.tenantDataSourceService = tenantDataSourceService;
//        this.tenantMissHandleInterfaces = tenantMissHandleInterfaces;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String tenantCode = "10001";
//        String tenantCode = this.getTenantCode(request);
        log.debug("tenant interceptor set tenant:{}", tenantCode);
        if (StringUtils.isBlank(tenantCode)) {
            log.warn("current tenant code not found : uri -> {}", request.getRequestURI());
//            throw new CommonException(ErrorCode.current_tenant_code_not_found);
        }
//        if (tenantDataSourceService.checkDataSource(tenantCode, null)) {
//            tenantDataSourceService.initDataSource(tenantCode, Constants.PROFILE_MYSQL);
//            tenantDataSourceService.initDataSourceInfoByTenantCode(tenantCode, Constants.PROFILE_MONGO);
//        }
//        // mongo he mysql 都没有配置则报异常
//        if (tenantDataSourceService.checkDataSource(tenantCode, null)) {
//            throw new CommonException(ErrorCode.cannot_get_data_source);
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
     *
     * @param request
     * @return
     */
//    private String getTenantCode(HttpServletRequest request) {
//        String tenantCode = request.getHeader(Constants.TENANT_CODE_KEY);
//        if (StringUtils.isBlank(tenantCode) && CollectionUtil.isNotEmpty(tenantMissHandleInterfaces)) {
//            for (TenantMissHandleInterface handleInterface : tenantMissHandleInterfaces) {
//                if (handleInterface.match(request)) {
//                    tenantCode = handleInterface.genTenantCode(request);
//                    break;
//                }
//            }
//        }
//        return tenantCode;
//    }
}
