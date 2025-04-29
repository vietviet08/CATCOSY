package com.catcosy.customer.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Autowired
    private EnvConfig envConfig;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.timeout:30}")
    private Integer timeout;

    @Bean
    public OpenAiService openAiService() {
        // Try to get API key from environment variables, then fall back to application.properties
        String apiKey = envConfig.getProperty("OPENAI_API_KEY", openaiApiKey);
        
        // Get timeout from environment or default to application.properties value
        String envTimeout = envConfig.getProperty("OPENAI_TIMEOUT", String.valueOf(timeout));
        Integer apiTimeout = timeout;
        
        try {
            apiTimeout = Integer.parseInt(envTimeout);
        } catch (NumberFormatException e) {
            // Ignore parsing errors and use default
        }
        
        return new OpenAiService(apiKey, Duration.ofSeconds(apiTimeout));
    }
}