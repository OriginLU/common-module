package com.zeroone.hystrix.server.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author zero-one.lu
 * @since 2021-05-01
 */
@Service
public class HystrixTestService {



    private final Logger log = LoggerFactory.getLogger(getClass());



    @HystrixCommand(
            groupKey = "findList-list-pool",
            commandKey = "findList",
            threadPoolKey = "findList-list-pool",
            commandProperties = @HystrixProperty(
            name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS,value = "5000"),
            threadPoolProperties ={ @HystrixProperty(
                    name = HystrixPropertiesManager.CORE_SIZE,value = "10"),
                    @HystrixProperty(
                            name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES,value = "2"),
                    @HystrixProperty(
                            name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD,value = "10"),
                    @HystrixProperty(
                            name = HystrixPropertiesManager.MAX_QUEUE_SIZE,value = "10")
            },
            fallbackMethod = "findListFallBack"
    )
    public List<String> findList(List<Long> ids){
        log.info("find list thread name:{}",Thread.currentThread().getName());
        return  ids.stream().map(id -> ("find id " + id)).collect(Collectors.toList());
    }

    public List<String> findListFallBack(List<Long> ids){
        return  ids.stream().map(id -> ("find List id FallBack " + id)).collect(Collectors.toList());
    }


    @HystrixCommand(
            groupKey = "findList-one-pool",
            commandKey = "findList",
            threadPoolKey = "findList-one-pool",
            commandProperties = @HystrixProperty(
                    name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS,value = "5000"),
            threadPoolProperties ={ @HystrixProperty(
                    name = HystrixPropertiesManager.CORE_SIZE,value = "10"),
                    @HystrixProperty(
                            name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES,value = "2"),
                    @HystrixProperty(
                            name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD,value = "10"),
                    @HystrixProperty(
                            name = HystrixPropertiesManager.MAX_QUEUE_SIZE,value = "10")
            },
            fallbackMethod = "findOneFallBack"
    )
    public String findOne(Long id) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
        log.info("find one thread name:{}",Thread.currentThread().getName());
        return "find one " + id;
    }

    public String findOneFallBack(Long id){
        return "find one fallback " + id;
    }
}
