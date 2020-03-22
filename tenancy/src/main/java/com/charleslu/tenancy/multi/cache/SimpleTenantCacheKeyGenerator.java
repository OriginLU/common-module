package com.charleslu.tenancy.multi.cache;

import com.charleslu.common.util.ThreadTenantUtil;
import com.charleslu.common.entity.TenantCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * 多租户缓存生成器
 *
 * @Author tanglh
 * @Date 2018/12/3 11:15
 */
public class SimpleTenantCacheKeyGenerator implements KeyGenerator {

    private static final Logger log = LoggerFactory.getLogger(SimpleTenantCacheKeyGenerator.class);

    @Override
    public Object generate(Object o, Method method, Object... objects) {
        Object result = new TenantCacheKey(ThreadTenantUtil.getTenant(), objects);
        log.debug("cacheKey {}", result);
        return result;
    }

}