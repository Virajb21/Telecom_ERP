package com.atharva.erp_telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Added this annotation for enabling the created and updated timestamps.
public class ErpTelecomApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpTelecomApplication.class, args);
	}

}
