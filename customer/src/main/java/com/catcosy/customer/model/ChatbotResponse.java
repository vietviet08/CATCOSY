package com.catcosy.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotResponse {
    private String message;
    private String userId;
    private boolean success;
    private List<Map<String, String>> conversationHistory;
    private String source; // "gemini", "local_knowledge", "error"
    
    @Builder.Default
    private String timestamp = String.valueOf(System.currentTimeMillis());
}