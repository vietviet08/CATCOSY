package com.catcosy.customer.config;

import com.catcosy.customer.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Lớp khởi tạo ứng dụng, chạy khi Spring Boot khởi động
 * Kiểm tra kết nối với Gemini API
 */
@Component
public class AppInitializer implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(AppInitializer.class.getName());
    
    @Autowired
    private GeminiService geminiService;
    
    @Autowired
    private GeminiConfig geminiConfig;

    @Override
    public void run(String... args) {
        // Only test the Gemini connection if the configuration allows it
        if (geminiConfig.isTestOnStartup()) {
            LOGGER.info("Testing Gemini API connection on startup...");
            boolean connectionSuccessful = geminiService.testGeminiConnection();
            
            if (connectionSuccessful) {
                LOGGER.info("✅ Gemini API connection successful - chatbot will use Gemini for responses");
            } else {
                LOGGER.warning("ℹ️ Gemini API connection unavailable - chatbot will use local knowledge base only");
            }
        } else {
            LOGGER.info("Gemini connection testing disabled - skipping startup test");
        }
    }
}