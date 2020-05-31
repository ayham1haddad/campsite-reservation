package com.upgrade.campsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@EnableRetry
public class CampsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampsiteApplication.class, args);
	}
}
