package com.zeroone.tenancy.hibernate.miss.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zero-one.lu
 * @since 2020-04-06
 */
public interface TenantCodeMissHandler {

    boolean match(HttpServletRequest request);

    /**
     *
     */
    String getTenantCode(HttpServletRequest request);
}
