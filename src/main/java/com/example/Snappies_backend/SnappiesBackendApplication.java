package com.example.Snappies_backend;

import com.example.Snappies_backend.models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = "com.example.Snappies_backend.repositories")
public class SnappiesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnappiesBackendApplication.class, args);
	}

}

