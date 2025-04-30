package com.catcosy.customer.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.logging.Logger;

@Configuration
public class EnvConfig {

    private static final Logger LOGGER = Logger.getLogger(EnvConfig.class.getName());
    private final Environment springEnv;
    
    public EnvConfig(Environment springEnv) {
        this.springEnv = springEnv;
    }
    
    @Bean
    public Dotenv dotenv() {
        File envFile = new File("customer/.env");
        String directory = "customer";
        
        if (!envFile.exists()) {
            envFile = new File(".env");
            directory = ".";
        }
        
        if (envFile.exists()) {
            LOGGER.info("Loading .env file from: " + envFile.getAbsolutePath());
            return Dotenv.configure()
                .directory(directory)
                .ignoreIfMalformed()
                .load();
        }
        
        LOGGER.warning("No .env file found. Using default configuration.");
        return Dotenv.configure().ignoreIfMissing().load();
    }
    
   
    public String getProperty(String key, String defaultValue) {
        try {
            String dotenvValue = dotenv().get(key);
            if (dotenvValue != null && !dotenvValue.isEmpty()) {
                return dotenvValue;
            }
        } catch (Exception e) {
            LOGGER.warning("Error reading from .env file: " + e.getMessage());
        }
        
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        value = springEnv.getProperty(key);
        return value != null ? value : defaultValue;
    }
}