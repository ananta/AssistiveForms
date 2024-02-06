package com.anntz.formservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.anntz.formservice.model"})
public class FormServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FormServiceApplication.class, args);
	}

}
