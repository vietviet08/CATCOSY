package com.catcosy.customer.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.catcosy.customer.config.EnvConfig;
import com.catcosy.customer.model.ChatMessage;
import com.catcosy.customer.model.ChatbotContext;
import com.catcosy.customer.util.ChatbotPromptUtil;
import com.catcosy.library.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class GeminiService {

    private CloseableHttpClient httpClient;

    private final EnvConfig envConfig;
    private final ChatbotProductService productService;
    private final ChatbotDatabaseLookupService databaseLookupService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${gemini.api.url:}")
    private String geminiApiUrl;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;
    
    // Keywords for product recommendation
    private static final List<String> PRODUCT_SEARCH_KEYWORDS = Arrays.asList(
        "tìm sản phẩm", "đề xuất", "recommendation", "sản phẩm", "products", 
        "mua", "buy", "áo", "quần", "váy", "giày", "túi", "order", "đặt hàng"
    );
    
    // Default product limit for recommendations
    private static final int DEFAULT_PRODUCT_LIMIT = 3;
    
    @PostConstruct
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    private String getApiKey() {
        String apiKey = envConfig.getProperty("gemini.api.key", geminiApiKey);
        return apiKey;
    }

    private String getApiUrl() {
        String apiKey = envConfig.getProperty("gemini.api.url", geminiApiUrl);
        return apiKey;
    }
    
    /**
     * Process user message and generate a response with product recommendations
     */    public String getResponse(String userMessage) throws Exception {
        // Check if this message might be related to product search
        boolean mightBeProductSearch = PRODUCT_SEARCH_KEYWORDS.stream()
            .anyMatch(keyword -> userMessage.toLowerCase().contains(keyword.toLowerCase()));
            
        // Get product recommendations if applicable
        List<ProductDto> recommendedProducts = new ArrayList<>();
        if (mightBeProductSearch) {
            recommendedProducts = productService.getRecommendedProducts(userMessage, DEFAULT_PRODUCT_LIMIT);
            
            // If no specific products found, get trending products
            if (recommendedProducts.isEmpty()) {
                recommendedProducts = productService.getTrendingProducts(DEFAULT_PRODUCT_LIMIT);
            }
        }
        
        // Build the Gemini API request
        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contents = objectMapper.createArrayNode();
        
        // Add system message
        ObjectNode systemMessage = objectMapper.createObjectNode();
        ArrayNode systemParts = objectMapper.createArrayNode();
        ObjectNode systemTextPart = objectMapper.createObjectNode();
        
        // Get store data to enhance the system prompt
        Map<String, Object> storeSummary = databaseLookupService.getStoreSummary();
        List<String> categories = databaseLookupService.getAllCategoryNames();
        List<String> brands = databaseLookupService.getAllBrandNames();
        
        // Create an enhanced system prompt with store data
        StringBuilder systemPrompt = new StringBuilder(ChatbotPromptUtil.getSystemPrompt());
        systemPrompt.append("\n\nThông tin về website CATCOSY:");
        systemPrompt.append("\n- Tổng số sản phẩm: " + storeSummary.getOrDefault("productCount", "nhiều"));
        systemPrompt.append("\n- Số danh mục: " + storeSummary.getOrDefault("categoryCount", "nhiều"));
        systemPrompt.append("\n- Số thương hiệu: " + storeSummary.getOrDefault("brandCount", "nhiều"));
        
        systemPrompt.append("\n\nDanh mục sản phẩm chính: ");
        if (categories != null && !categories.isEmpty()) {
            systemPrompt.append(String.join(", ", categories.subList(0, Math.min(categories.size(), 10))));
        } else {
            systemPrompt.append("nhiều danh mục đa dạng");
        }
        
        systemPrompt.append("\n\nThương hiệu nổi bật: ");
        if (brands != null && !brands.isEmpty()) {
            systemPrompt.append(String.join(", ", brands.subList(0, Math.min(brands.size(), 10))));
        } else {
            systemPrompt.append("nhiều thương hiệu nổi tiếng");
        }
        
        systemTextPart.put("text", systemPrompt.toString());
        systemParts.add(systemTextPart);
        systemMessage.put("role", "model");
        systemMessage.set("parts", systemParts);
        contents.add(systemMessage);
        
        // Add user message
        ObjectNode userMessageObj = objectMapper.createObjectNode();
        ArrayNode userParts = objectMapper.createArrayNode();
        ObjectNode userTextPart = objectMapper.createObjectNode();
        
        // Format user message with product context if available
        StringBuilder enhancedMessage = new StringBuilder(userMessage);
        if (!recommendedProducts.isEmpty()) {
            enhancedMessage.append("\n\nContext: Here are some products from our database that might match the query:");
            int index = 1;
            for (ProductDto product : recommendedProducts) {
                enhancedMessage.append("\n\nProduct " + index + ":");
                enhancedMessage.append("\nName: " + product.getName());
                enhancedMessage.append("\nPrice: " + product.getCostPrice() + " VND");
                if (product.getSalePrice() > 0) {
                    enhancedMessage.append(" (Sale: " + product.getSalePrice() + " VND)");
                }
                enhancedMessage.append("\nDescription: " + (product.getDescription() != null ? 
                        (product.getDescription().length() > 100 ? 
                                product.getDescription().substring(0, 100) + "..." : 
                                product.getDescription()) : 
                        "No description"));
                enhancedMessage.append("\nCategory: " + (product.getCategory() != null ? 
                        product.getCategory().getName() : "Unknown"));
                enhancedMessage.append("\nBrand: " + (product.getBrand() != null ? 
                        product.getBrand().getName() : "Unknown"));
                
                if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                    enhancedMessage.append("\nImage: " + product.getImageUrls().get(0));
                }
                
                index++;
            }
        }
        
        userTextPart.put("text", enhancedMessage.toString());
        userParts.add(userTextPart);
        userMessageObj.put("role", "user");
        userMessageObj.set("parts", userParts);
        contents.add(userMessageObj);
        
        requestBody.set("contents", contents);
        requestBody.put("generationConfig", objectMapper.createObjectNode()
                .put("temperature", 0.7)
                .put("topK", 40)
                .put("topP", 0.95)
                .put("maxOutputTokens", 800));
        
        // Send the request
        HttpPost post = new HttpPost(getApiUrl() + "?key=" + getApiKey());
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        
        log.debug("Sending request to Gemini API: {}", requestBody.toString());

        try (var response = httpClient.execute(post)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            log.debug("Received response from Gemini API: {}", result.toString());
            
            // Parse the response to extract the generated text
            var jsonNode = objectMapper.readTree(result.toString());
            String responseText = jsonNode.at("/candidates/0/content/parts/0/text").asText();
            
            // Format the response to include product details if needed
            if (!recommendedProducts.isEmpty() && !responseText.contains("VND")) {
                String productListText = recommendedProducts.stream()
                    .map(p -> {
                        String price = p.getSalePrice() > 0 ? 
                            p.getCostPrice() + " VND (Giảm giá: " + p.getSalePrice() + " VND)" :
                            p.getCostPrice() + " VND";
                        return "- " + p.getName() + ": " + price;
                    })
                    .collect(Collectors.joining("\n"));
                
                return responseText + "\n\n" + productListText;
            }
            
            return responseText;
        }
    }    public String checkApiKey() {
        HttpPost post = new HttpPost(getApiUrl() + "?key=" + getApiKey());
        System.out.println("API URL: " + getApiUrl() + "?key=" + getApiKey());
        post.setHeader("Content-Type", "application/json");

        // Create a test request with system prompt to validate both API key and the chatbot's behavior
        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contents = objectMapper.createArrayNode();
        
        // Add system message
        ObjectNode systemMessage = objectMapper.createObjectNode();
        ArrayNode systemParts = objectMapper.createArrayNode();
        ObjectNode systemTextPart = objectMapper.createObjectNode();
        systemTextPart.put("text", ChatbotPromptUtil.getSystemPrompt());
        systemParts.add(systemTextPart);
        systemMessage.put("role", "model");
        systemMessage.set("parts", systemParts);
        contents.add(systemMessage);
        
        // Add user message
        ObjectNode userMessageObj = objectMapper.createObjectNode();
        ArrayNode userParts = objectMapper.createArrayNode();
        ObjectNode userTextPart = objectMapper.createObjectNode();
        userTextPart.put("text", "Xin chào, bạn là ai?");
        userParts.add(userTextPart);
        userMessageObj.put("role", "user");
        userMessageObj.set("parts", userParts);
        contents.add(userMessageObj);
        
        requestBody.set("contents", contents);
        requestBody.put("generationConfig", objectMapper.createObjectNode()
                .put("temperature", 0.7)
                .put("topK", 40)
                .put("topP", 0.95)
                .put("maxOutputTokens", 800));
                
        try {
            post.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
            try (var response = httpClient.execute(post)) {
                int statusCode = response.getCode();
                
                if (statusCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    
                    try {
                        var jsonNode = objectMapper.readTree(result.toString());
                        String responseText = jsonNode.at("/candidates/0/content/parts/0/text").asText();
                        return "API Key is valid. Response: " + responseText;
                    } catch (Exception e) {
                        return "API Key is valid but there was an error parsing the response.";
                    }
                } else if (statusCode == 401 || statusCode == 403) {
                    return "API Key is invalid or unauthorized. Status code: " + statusCode;
                } else {
                    return "Unexpected response. Status code: " + statusCode;
                }
            }
        } catch (Exception e) {
            return "Error checking API Key: " + e.getMessage();
        }
    }
}
