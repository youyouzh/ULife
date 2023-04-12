package com.uusama.ulife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author uusama
 */
@SpringBootApplication(scanBasePackages = {"${uusama.info.base-package}.server", "${uusama.info.base-package}.module"})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
