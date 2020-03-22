package com.charleslu.tenancy.multi.util;

import com.taoqi.common.util.ThreadContextUtil;
import com.taoqi.common.util.ThreadTenantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * @author ziyan
 * @email zhengmengyan@taoqicar.com
 * @date 2018年12月11日
 */
public abstract class AbstractExecutorProxy implements Executor, InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(AbstractExecutorProxy.class);

    protected abstract Executor getExecutor();

    @Override
    public void execute(Runnable task) {
        this.getExecutor().execute(this.createWrappedRunnable(task));
    }

    protected <T> Callable<T> createCallable(final Callable<T> task) {
        final String currentTenant = ThreadTenantUtil.getTenant();
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return () -> {
            try {
                ThreadTenantUtil.setTenant(currentTenant);
                ThreadContextUtil.setContext(securityContext);
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            } finally {
                ThreadTenantUtil.remove();
                ThreadContextUtil.clearContext();
            }
        };
    }

    protected Runnable createWrappedRunnable(final Runnable task) {
        final String currentTenant = ThreadTenantUtil.getTenant();
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return () -> {
            try {
                ThreadTenantUtil.setTenant(currentTenant);
                ThreadContextUtil.setContext(securityContext);
                task.run();
            } catch (Exception e) {
                handle(e);
                throw e;
            } finally {
                ThreadTenantUtil.remove();
                ThreadContextUtil.clearContext();
            }
        };
    }


    protected void handle(Exception e) {
        log.error("Caught exception", e);
    }
}
