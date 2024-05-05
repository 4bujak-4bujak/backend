package com.example.sabujak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SabujakApplication {

	public static void main(String[] args) {
		SpringApplication.run(SabujakApplication.class, args);
	}

}
