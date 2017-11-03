package com.appdirect.migration.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.appdirect.migration.service.core.QueueConfiguration;

@SpringBootApplication
@Import({ QueueConfiguration.class})
public class MigrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MigrationServiceApplication.class, args);
	}
}
