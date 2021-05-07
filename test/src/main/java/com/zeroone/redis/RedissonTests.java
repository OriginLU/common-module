package com.zeroone.redis;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedissonTests {


    public final static Logger log = LoggerFactory.getLogger(RedissonTests.class);

    public static void main(String[] args) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.0.25.169:6379").setPassword("Taoqi123");
        config.setLockWatchdogTimeout(30000L);
        RedissonClient redissonClient = Redisson.create(config);
        RLock lock = redissonClient.getLock("test-lock");

        try {
            lock.lock();
            log.info("lock ...");
            Thread.sleep(60000L);
        }catch (Exception e){
            log.error("redis error",e);
        }finally {
            log.info("unlock");
            lock.unlock();
        }
        redissonClient.shutdown();


    }
}
