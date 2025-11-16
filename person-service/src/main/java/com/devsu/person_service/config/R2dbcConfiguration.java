package com.devsu.person_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.devsu.person_service.adapter.out.persistence")
@EnableTransactionManagement
public class R2dbcConfiguration {
    // Transaction management is automatically configured by Spring Boot
    // This class explicitly enables it for clarity
}
