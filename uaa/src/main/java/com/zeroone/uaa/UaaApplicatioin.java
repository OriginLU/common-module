package com.zeroone.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zero-one.lu
 * @since 2020-12-02
 */
@SpringBootApplication
//@EnableDiscoveryClient
//@EnableHystrix
//@EnableFeignClients
public class UaaApplicatioin {

    public static void main(String[] args) {
        SpringApplication.run(UaaApplicatioin.class,args);
    }
}
