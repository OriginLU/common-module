package com.charleslu.tenancy.interceptor;

import com.hazelcast.util.CollectionUtil;
import com.charleslu.tenancy.annotation.WrapperRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 用于请求包装的过滤器
 *
 * @author ziyan
 * @email zhengmengyan@charleslucar.com
 * @date 2018年12月29日
 */
@Component
public class RequestWrapperFilter extends GenericFilterBean {

    @Autowired(required = false)
    private Set<TenantMissHandleInterface> tenantMissHandleInterfaceSet;

    @PostConstruct
    public void init() {
        if (CollectionUtil.isNotEmpty(tenantMissHandleInterfaceSet)) {
            Set<TenantMissHandleInterface> set = new LinkedHashSet<>(tenantMissHandleInterfaceSet.size());
            for (TenantMissHandleInterface o : tenantMissHandleInterfaceSet) {
                WrapperRequest wrapperRequest = AnnotationUtils.findAnnotation(o.getClass(), WrapperRequest.class);
                if (wrapperRequest != null) {
                    set.add(o);
                }
            }
            this.tenantMissHandleInterfaceSet = set;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest currentRequest = (HttpServletRequest) request;
        if (CollectionUtil.isNotEmpty(tenantMissHandleInterfaceSet)) {
            for (TenantMissHandleInterface o : tenantMissHandleInterfaceSet) {
                if (o.match(currentRequest)) {
                    currentRequest = new MyHttpServletRequestWrapper(currentRequest);
                    break;
                }
            }
        }
        chain.doFilter(currentRequest, response);
    }
}
