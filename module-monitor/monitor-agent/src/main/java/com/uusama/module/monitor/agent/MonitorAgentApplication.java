package com.uusama.module.monitor.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhaohai
 */
@EnableAsync
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.cjhx.monitor.client"})
@EnableScheduling
@SpringBootApplication
public class MonitorAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorAgentApplication.class, args);
    }

}
