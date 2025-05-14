package com.catcosy.customer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.catcosy.customer.model.ChatMessage;
import com.catcosy.customer.model.ChatbotContext;
import com.catcosy.customer.service.ChatbotProductService;
import com.catcosy.customer.service.GeminiService;
import com.catcosy.library.dto.ProductDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final GeminiService geminiService;
    private final ChatbotProductService productService;

    @GetMapping("/chatbot")
    public String showChatbot(Model model) {
        model.addAttribute("messages", new ArrayList<ChatMessage>());
        return "chatbot";
    }

    @PostMapping("/chatbot/send")
    @ResponseBody
    public ChatMessage sendMessage(@RequestBody ChatMessage userMessage) {
        try {
            log.info("Received message: {}", userMessage.getContent());
            
            // Get response from Gemini API
            String botResponse = geminiService.getResponse(userMessage.getContent());
            
            // Create response
            ChatMessage response = new ChatMessage();
            response.setSender("Bot");
            response.setContent(botResponse);
            
            // Check if this is a product search query
            if (isProductSearchQuery(userMessage.getContent())) {
                // Get recommended products
                List<ProductDto> recommendedProducts = productService.getRecommendedProducts(
                    userMessage.getContent(), 5);
                
                if (!recommendedProducts.isEmpty()) {
                    // Convert ProductDto to ChatbotContext.Product
                    ChatbotContext context = new ChatbotContext();
                    context.setType("products");
                    
                    for (ProductDto productDto : recommendedProducts) {
                        ChatbotContext.Product product = convertToChatbotProduct(productDto);
                        context.addProduct(product);
                    }
                    
                    response.setContext(context);
                }
            }
            
            return response;
        } catch (Exception e) {
            log.error("Error processing chatbot message", e);
            ChatMessage errorResponse = new ChatMessage();
            errorResponse.setSender("Bot");
            errorResponse.setContent("Xin lỗi, đã xảy ra lỗi. Vui lòng thử lại sau.");
            return errorResponse;
        }
    }
    
    @GetMapping("/chatbot/products")
    @ResponseBody
    public ChatMessage getProductRecommendations(@RequestParam String query) {
        try {
            // Get recommended products
            List<ProductDto> recommendedProducts = productService.getRecommendedProducts(query, 5);
            
            ChatMessage response = new ChatMessage();
            response.setSender("Bot");
            
            if (!recommendedProducts.isEmpty()) {
                response.setContent("Đây là một số sản phẩm phù hợp với yêu cầu của bạn:");
                
                // Convert ProductDto to ChatbotContext.Product
                ChatbotContext context = new ChatbotContext();
                context.setType("products");
                
                for (ProductDto productDto : recommendedProducts) {
                    ChatbotContext.Product product = convertToChatbotProduct(productDto);
                    context.addProduct(product);
                }
                
                response.setContext(context);
            } else {
                response.setContent("Xin lỗi, tôi không tìm thấy sản phẩm nào phù hợp với yêu cầu của bạn.");
            }
            
            return response;
        } catch (Exception e) {
            log.error("Error getting product recommendations", e);
            ChatMessage errorResponse = new ChatMessage();
            errorResponse.setSender("Bot");
            errorResponse.setContent("Xin lỗi, đã xảy ra lỗi khi tìm kiếm sản phẩm. Vui lòng thử lại sau.");
            return errorResponse;
        }
    }

    @GetMapping("/chatbot/check-api-key")
    @ResponseBody
    public String checkApiKey() {
        return geminiService.checkApiKey();
    }
    
    /**
     * Check if the message appears to be a product search query
     */
    private boolean isProductSearchQuery(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("sản phẩm") || lowerMessage.contains("mua") || 
               lowerMessage.contains("product") || lowerMessage.contains("áo") ||
               lowerMessage.contains("quần") || lowerMessage.contains("váy") ||
               lowerMessage.contains("giày") || lowerMessage.contains("túi") ||
               lowerMessage.contains("đề xuất") || lowerMessage.contains("recommend");
    }
    
    /**
     * Convert ProductDto to ChatbotContext.Product
     */
    private ChatbotContext.Product convertToChatbotProduct(ProductDto productDto) {
        ChatbotContext.Product product = new ChatbotContext.Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        
        // Truncate description if needed
        if (productDto.getDescription() != null) {
            String description = productDto.getDescription();
            if (description.length() > 100) {
                description = description.substring(0, 97) + "...";
            }
            product.setDescription(description);
        }
        
        // Set category and brand
        if (productDto.getCategory() != null) {
            product.setCategory(productDto.getCategory().getName());
        }
        
        if (productDto.getBrand() != null) {
            product.setBrand(productDto.getBrand().getName());
        }
        
        // Set image URLs
        if (productDto.getImageUrls() != null && !productDto.getImageUrls().isEmpty()) {
            product.setImageUrls(productDto.getImageUrls());
        }
        
        return product;
    }
}
