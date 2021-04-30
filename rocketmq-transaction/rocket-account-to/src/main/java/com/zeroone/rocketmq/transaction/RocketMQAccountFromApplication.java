package com.zeroone.rocketmq.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.zeroone.rocketmq.transaction.entity")
@EnableJpaRepositories("com.zeroone.rocketmq.transaction.repository")
public class RocketMQAccountFromApplication {


    public static void main(String[] args) {
        SpringApplication.run(RocketMQAccountFromApplication.class,args);
    }
}
