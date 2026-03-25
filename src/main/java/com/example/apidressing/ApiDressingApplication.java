package com.example.apidressing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ApiDressingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDressingApplication.class, args);
	}

}
