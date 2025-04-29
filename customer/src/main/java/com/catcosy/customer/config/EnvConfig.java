package com.catcosy.customer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Configuration class for environment variables.
 * This simpler approach uses Spring's built-in functionality rather than an external library.
 */
@Configuration
public class EnvConfig {

    private final Environment environment;
    
    @Autowired
    public EnvConfig(Environment environment) {
        this.environment = environment;
    }
    
    /**
     * Gets an environment variable or property value, with fallback to Spring properties
     * 
     * @param key The environment variable or property name
     * @param defaultValue The default value if not found
     * @return The value of the environment variable/property or the default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = environment.getProperty(key);
        }
        return value != null ? value : defaultValue;
    }
}