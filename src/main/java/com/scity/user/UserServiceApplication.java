package com.scity.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.scity.user.*"})
@Validated
@EnableCaching
@EnableScheduling
@Slf4j
public class UserServiceApplication {
  public static void main(String[] args) {
      System.setProperty("spring.devtools.restart.enabled", "false");
      SpringApplication.run(UserServiceApplication.class, args);
	}
}
