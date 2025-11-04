package com.example.endavapwj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.endavapwj.collection")
@EnableJpaRepositories("com.example.endavapwj.repositories")
public class EndavaPwjApplication {

	public static void main(String[] args) {
		SpringApplication.run(EndavaPwjApplication.class, args);
	}

}
