package com.dacs1.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.dacs1.library.*", "com.dacs1.customer.*"})
@EnableJpaRepositories(value = "com.dacs1.library.repository")
@EntityScan(value = "com.dacs1.library.model")
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

}
