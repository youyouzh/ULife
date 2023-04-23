package com.uusama.module.monitor.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author zhaohai
 */
@EnableEurekaClient
@SpringBootApplication
public class MonitorExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorExampleApplication.class, args);
    }

}
