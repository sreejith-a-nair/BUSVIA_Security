package com.busvia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@OpenAPIDefinition(info=@Info(title="Auth Apis",version = "1.0",description = "Auth management Apis."))

public class BusViaSecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusViaSecurityServiceApplication.class, args);
	}

}
