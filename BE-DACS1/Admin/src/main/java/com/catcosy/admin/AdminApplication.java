package com.dacs1.admin;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.dacs1.library.*", "com.dacs1.admin.*"})
@EnableJpaRepositories(value = {"com.dacs1.library.repository"})
@EntityScan(value = {"com.dacs1.library.model"})
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
