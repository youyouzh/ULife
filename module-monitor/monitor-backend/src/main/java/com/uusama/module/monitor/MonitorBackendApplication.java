package com.uusama.module.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhaohai
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"${uusama.info.base-package}.server", "${uusama.info.base-package}.module"})
public class MonitorBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitorBackendApplication.class, args);
	}

}
