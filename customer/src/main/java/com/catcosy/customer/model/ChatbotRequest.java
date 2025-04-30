package com.catcosy.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    private String message;
    private String userId;
    private List<Map<String, String>> conversationHistory = new ArrayList<>();
    
    /**
     * Thêm tin nhắn vào lịch sử cuộc trò chuyện
     * 
     * @param role Vai trò (user/assistant/system)
     * @param content Nội dung tin nhắn
     */
    public void addToHistory(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        this.conversationHistory.add(message);
    }
}