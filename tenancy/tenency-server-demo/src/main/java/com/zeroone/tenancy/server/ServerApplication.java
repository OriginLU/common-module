package com.zeroone.tenancy.server;

import com.zeroone.tenancy.hibernate.annotation.EnableTenancyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zero-one.lu
 * @since 2020-04-05
 */
@EnableTenancyServer
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class,args);
    }
}
