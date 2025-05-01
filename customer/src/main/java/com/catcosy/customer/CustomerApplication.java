package com.catcosy.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.catcosy.library.*", "com.catcosy.customer.*"})
@EnableJpaRepositories(value = "com.catcosy.library.repository")
@EntityScan(value = "com.catcosy.library.model")
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

}