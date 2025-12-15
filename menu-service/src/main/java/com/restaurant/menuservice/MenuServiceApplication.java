package com.restaurant.menuservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "com.restaurant.menuservice",
        "com.restaurant.filter_module.jwt.client"
})
@ComponentScan(basePackages = {
        "com.restaurant.menuservice",
        "com.restaurant.redismodule",
        "com.restaurant.factorymodule",
        "com.restaurant.kafkamodule",
        "com.restaurant.filter_module.core",
        "com.restaurant.filter_module.jwt",
        "com.restaurant.data"
})
public class MenuServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenuServiceApplication.class, args);
	}

}
