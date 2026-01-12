package com.example.endavapwj.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.endavapwj.repositories")
public class JPAConfig {}
