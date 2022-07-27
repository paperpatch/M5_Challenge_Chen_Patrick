package com.company.gamestorecatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GamestoreCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamestoreCatalogApplication.class, args);
	}

}
