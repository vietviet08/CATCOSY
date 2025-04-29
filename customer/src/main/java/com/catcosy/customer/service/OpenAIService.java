package com.catcosy.customer.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OpenAIService {

    private static final Logger LOGGER = Logger.getLogger(OpenAIService.class.getName());
    private boolean quotaExceeded = false;

    @Autowired
    private OpenAiService openAiService;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${openai.max-tokens:300}")
    private Integer maxTokens;

    /**
     * Generate a response using OpenAI's GPT model
     * 
     * @param prompt The user's message
     * @param context Previous conversation history
     * @return Generated response from GPT
     */
    public String generateResponse(String prompt, List<ChatMessage> context) {
        // Skip API call if quota is already known to be exceeded
        if (quotaExceeded) {
            LOGGER.info("Skipping OpenAI API call due to previously exceeded quota");
            return "I'll help you with what I know locally, as our AI service is currently unavailable.";
        }
        
        try {
            List<ChatMessage> messages = new ArrayList<>(context);
            
            // Add system message for context about CATCOSY being a fashion e-commerce website
            if (context.isEmpty()) {
                messages.add(new ChatMessage("system", 
                    "You are an AI assistant for CATCOSY, a fashion e-commerce website. " +
                    "Provide helpful, concise information about fashion products, trends, " +
                    "shopping advice, and answer customer questions about the CATCOSY store. " +
                    "Keep responses under 100 words. Be friendly and professional."
                ));
            }
            
            // Add the user's current query
            messages.add(new ChatMessage("user", prompt));
            
            // Create the completion request
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model(model)
                    .maxTokens(maxTokens)
                    .temperature(0.7)
                    .build();
            
            LOGGER.info("Sending request to OpenAI API with model: " + model);
            
            // Call the OpenAI API and get the response
            var completion = openAiService.createChatCompletion(completionRequest);
            
            if (completion == null || completion.getChoices() == null || completion.getChoices().isEmpty()) {
                LOGGER.warning("Received null or empty response from OpenAI API");
                return "I'm having trouble getting a response. Please try again shortly.";
            }
            
            return completion.getChoices().get(0).getMessage().getContent();
            
        } catch (Exception e) {
            // Check for quota exceeded error specifically
            if (e.getMessage() != null && 
                (e.getMessage().contains("exceeded your current quota") || 
                 e.getMessage().contains("429") || 
                 e.getMessage().contains("rate limit"))) {
                
                // Set the flag to avoid further API calls
                quotaExceeded = true;
                LOGGER.severe("OpenAI quota exceeded. Will use local knowledge base for future requests.");
                return "I'll help you with what I know locally, as our AI service is currently unavailable.";
            }
            
            // Log the detailed error
            LOGGER.log(Level.SEVERE, "Error calling OpenAI API: " + e.getMessage(), e);
            
            // Return a user-friendly message based on error type
            if (e.getMessage() != null) {
                if (e.getMessage().contains("401") || e.getMessage().contains("authentication")) {
                    return "I'm having trouble with my AI connection due to authentication issues. Please contact the administrator to check the API key configuration.";
                } else if (e.getMessage().contains("timeout")) {
                    return "The response is taking longer than expected. Please try a simpler question or try again later.";
                }
            }
            
            // General fallback message
            return "I'm having trouble connecting to my knowledge base. Let me help you with what I know locally.";
        }
    }
    
    /**
     * Convert conversation history from the chatbot format to OpenAI's format
     * 
     * @param history The conversation history from the chatbot
     * @return List of ChatMessage objects for OpenAI API
     */
    public List<ChatMessage> convertHistoryToMessages(List<Map<String, String>> history) {
        List<ChatMessage> messages = new ArrayList<>();
        
        if (history != null) {
            for (Map<String, String> entry : history) {
                String role = entry.getOrDefault("role", "user");
                String content = entry.getOrDefault("content", "");
                
                // Convert to OpenAI's expected format
                if ("assistant".equals(role) || "user".equals(role)) {
                    messages.add(new ChatMessage(role, content));
                }
            }
        }
        
        return messages;
    }
}