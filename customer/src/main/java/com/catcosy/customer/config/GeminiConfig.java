package com.catcosy.customer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class GeminiConfig {

    private static final Logger LOGGER = Logger.getLogger(GeminiConfig.class.getName());

    @Autowired
    private EnvConfig envConfig;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-1.0-pro}")
    private String model;
    
    @Value("${gemini.max-tokens:300}")
    private Integer maxTokens;
    
    @Value("${gemini.test-on-startup:true}")
    private boolean testOnStartup;
    
    @Value("${gemini.project:}")
    private String project;
    
    @Value("${gemini.location:us-central1}")
    private String location;

    /**
     * Lấy API key từ các nguồn cấu hình
     * @return API key của Gemini
     */
    public String getApiKey() {
        // Ưu tiên lấy API key từ file .env
        String apiKey = envConfig.getProperty("gemini.api.key", geminiApiKey);
        return apiKey;
    }
    
    /**
     * Lấy tên model Gemini được cấu hình
     * @return Tên model Gemini
     */
    public String getModel() {
        // Lấy model từ .env hoặc application.properties
        return envConfig.getProperty("gemini.model", model);
    }
    
    /**
     * Lấy giới hạn tokens
     * @return Số lượng tokens tối đa
     */
    public Integer getMaxTokens() {
        String envMaxTokens = envConfig.getProperty("gemini.max-tokens", maxTokens.toString());
        try {
            return Integer.parseInt(envMaxTokens);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid max tokens value: " + envMaxTokens + ". Using default: " + maxTokens);
            return maxTokens;
        }
    }

    /**
     * Lấy cấu hình kiểm tra kết nối khi khởi động
     * @return true nếu cần kiểm tra, false nếu không
     */
    public boolean isTestOnStartup() {
        String envTestOnStartup = envConfig.getProperty("gemini.test-on-startup", String.valueOf(testOnStartup));
        return Boolean.parseBoolean(envTestOnStartup);
    }
    
    /**
     * Lấy tên project Google Cloud
     * @return Tên project
     */
    public String getProject() {
        return envConfig.getProperty("gemini.project", project);
    }
    
    /**
     * Lấy vị trí API Gemini
     * @return Location của API
     */
    public String getLocation() {
        return envConfig.getProperty("gemini.location", location);
    }
}