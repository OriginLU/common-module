package com.charleslu.tenancy.multi.cache;

import com.charleslu.tenancy.multi.util.ThreadTenantUtil;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCache;
import com.charleslu.common.util.ThreadTenantUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Hazelcast缓存管理器
 *
 * @Author tanglh
 * @Date 2018/12/27 15:49
 */
public class TenantHazelcastCacheManager implements CacheManager {
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap();
    private HazelcastInstance hazelcastInstance;

    private static final String TENANT_KEY = "tenant(%s)->%s";
    private static final Pattern PATTERN = Pattern.compile("tenant\\(.+\\)->");
    private static final String NULL = "null";

    public TenantHazelcastCacheManager() {
    }

    public TenantHazelcastCacheManager(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Cache getCache(String name) {
        String tenant = Optional.ofNullable(ThreadTenantUtil.getTenant()).orElse("system");
        String tenantCacheName = String.format(TENANT_KEY, tenant, name == null ? NULL : name);

        Cache cache = this.caches.get(tenantCacheName);
        if (cache == null) {
            IMap map = this.hazelcastInstance.getMap(tenantCacheName);
            cache = new HazelcastCache(map);
            Cache currentCache = this.caches.putIfAbsent(tenantCacheName, cache);
            if (currentCache != null) {
                cache = currentCache;
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        HashSet cacheNames = new HashSet();
        Collection distributedObjects = this.hazelcastInstance.getDistributedObjects();
        Iterator i$ = distributedObjects.iterator();

        while (i$.hasNext()) {
            DistributedObject distributedObject = (DistributedObject) i$.next();
            if (distributedObject instanceof IMap) {
                IMap map = (IMap) distributedObject;

                String name = map.getName();
                name = PATTERN.matcher(name).replaceFirst("");
                if (StringUtils.equals(name, NULL)) {
                    name = null;
                }
                cacheNames.add(name);
            }
        }

        return cacheNames;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public HazelcastInstance getHazelcastInstance() {
        return this.hazelcastInstance;
    }

}