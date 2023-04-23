package com.uusama.module.monitor.eureka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author zhaohai
 */
@Slf4j
@EnableEurekaServer
@SpringBootApplication
public class MonitorEurekaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        log.info("MonitorEurekaApplication Start.....");
        SpringApplication.run(MonitorEurekaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("CommandLineRunner ...");
    }
}
