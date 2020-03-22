package com.charleslu.tenancy.interceptor;

import com.charleslu.tenancy.multi.util.ThreadTenantUtil;
import com.hazelcast.util.CollectionUtil;
import com.charleslu.common.constants.Constants;
import com.charleslu.common.errors.CommonException;
import com.charleslu.common.errors.ErrorCode;
import com.charleslu.common.util.ThreadTenantUtil;
import com.charleslu.tenancy.service.TenantDataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 租户拦截器
 *
 * @Author tanglh
 * @Date 2018/11/30 13:12
 */
public class TenantInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private final TenantDataSourceService tenantDataSourceService;

    private final Set<TenantMissHandleInterface> tenantMissHandleInterfaces;

    public TenantInterceptor(TenantDataSourceService tenantDataSourceService, Set<TenantMissHandleInterface> tenantMissHandleInterfaces) {
        this.tenantDataSourceService = tenantDataSourceService;
        this.tenantMissHandleInterfaces = tenantMissHandleInterfaces;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String tenantCode = this.getTenantCode(request);
        log.debug("TenantInterceptor setTenant:{}", tenantCode);
        if (StringUtils.isBlank(tenantCode)) {
            log.warn("current_tenant_code_not_found : uri -> {}", request.getRequestURI());
            throw new CommonException(ErrorCode.current_tenant_code_not_found);
        }
        if (tenantDataSourceService.checkDataSource(tenantCode, null)) {
            tenantDataSourceService.initDataSourceInfoByTenantCode(tenantCode, Constants.PROFILE_MYSQL);
            tenantDataSourceService.initDataSourceInfoByTenantCode(tenantCode, Constants.PROFILE_MONGO);
        }
        // mongo he mysql 都没有配置则报异常
        if (tenantDataSourceService.checkDataSource(tenantCode, null)) {
            throw new CommonException(ErrorCode.cannot_get_data_source);
        }
        //设置租户信息
        ThreadTenantUtil.setTenant(tenantCode);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        log.debug("TenantInterceptor removeTenant:{}", ThreadTenantUtil.getTenant());
        //释放资源
        ThreadTenantUtil.remove();
    }

    /**
     * 获取租户号
     *
     * @param request
     * @return
     */
    private String getTenantCode(HttpServletRequest request) {
        String tenantCode = request.getHeader(Constants.TENANT_CODE_KEY);
        if (StringUtils.isBlank(tenantCode) && CollectionUtil.isNotEmpty(tenantMissHandleInterfaces)) {
            for (TenantMissHandleInterface handleInterface : tenantMissHandleInterfaces) {
                if (handleInterface.match(request)) {
                    tenantCode = handleInterface.genTenantCode(request);
                    break;
                }
            }
        }
        return tenantCode;
    }
}