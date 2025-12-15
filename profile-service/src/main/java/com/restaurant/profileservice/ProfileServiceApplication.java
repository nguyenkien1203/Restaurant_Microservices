package com.restaurant.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = {
		"com.restaurant.profileservice",
		"com.restaurant.filter_module.jwt.client"
})
@ComponentScan(basePackages = {
		"com.restaurant.profileservice",
		"com.restaurant.redismodule",
		"com.restaurant.factorymodule",
		"com.restaurant.kafkamodule",
		"com.restaurant.filter_module.core",
		"com.restaurant.filter_module.jwt",
		"com.restaurant.data"
})
public class ProfileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileServiceApplication.class, args);
	}

}
