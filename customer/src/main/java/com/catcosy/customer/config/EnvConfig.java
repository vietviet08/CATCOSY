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
        // Tìm file .env trong thư mục customer
        File envFile = new File("customer/.env");
        String directory = "customer";
        
        if (!envFile.exists()) {
            // Nếu không tìm thấy trong thư mục customer, thử tìm trong thư mục gốc
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
    
    /**
     * Gets an environment variable or property value, with fallback to Spring properties
     * 
     * @param key The environment variable or property name
     * @param defaultValue The default value if not found
     * @return The value of the environment variable/property or the default value
     */
    public String getProperty(String key, String defaultValue) {
        // Kiểm tra trong file .env trước
        try {
            String dotenvValue = dotenv().get(key);
            if (dotenvValue != null && !dotenvValue.isEmpty()) {
                return dotenvValue;
            }
        } catch (Exception e) {
            LOGGER.warning("Error reading from .env file: " + e.getMessage());
        }
        
        // Kiểm tra biến môi trường hệ thống
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // Cuối cùng, kiểm tra trong application.properties
        value = springEnv.getProperty(key);
        return value != null ? value : defaultValue;
    }
}