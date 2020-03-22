package com.charleslu.tenancy.interceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于请求缺失租户信息的处理
 *
 * @author ziyan
 * @email zhengmengyan@charleslucar.com
 * @date 2018年12月29日
 */
public interface TenantMissHandleInterface {

    /**
     * 是否需要处理
     *
     * @param request
     * @return
     */
    boolean match(HttpServletRequest request);

    /**
     * 生成租户id
     *
     * @param request
     * @return
     */
    String genTenantCode(HttpServletRequest request);
}
