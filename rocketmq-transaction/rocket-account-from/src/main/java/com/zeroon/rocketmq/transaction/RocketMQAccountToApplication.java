package com.zeroon.rocketmq.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.zeroon.rocketmq.transaction.entity")
@EnableJpaRepositories("com.zeroon.rocketmq.transaction.repository")
public class RocketMQAccountToApplication {


    public static void main(String[] args) {

        SpringApplication.run(RocketMQAccountToApplication.class,args);
    }
}
