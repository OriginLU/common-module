package com.charleslu.tenancy.multi.util;

/**
 * 租户线程工具类
 *
 * @Author tanglh
 * @Date 2018/11/29 18:10
 */
public class ThreadTenantUtil {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程对应的租户
     *
     * @param tenantCode
     */
    public static void setTenant(String tenantCode) {
        if (null != tenantCode) {
            threadLocal.set(tenantCode);
        }
    }

    /**
     * 获取当前线程对应的租户
     *
     * @return
     */
    public static String getTenant() {
        return threadLocal.get();
    }

    /**
     * 删除线程变量
     */
    public static void remove() {
        threadLocal.remove();
    }
}