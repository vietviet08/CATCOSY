package com.catcosy.customer.service;

import com.catcosy.customer.model.ChatbotRequest;
import com.catcosy.customer.model.ChatbotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class ChatbotService {

    private static final Logger LOGGER = Logger.getLogger(ChatbotService.class.getName());
    private static final int MAX_HISTORY_SIZE = 20; // Giới hạn số tin nhắn trong lịch sử
    
    // Lưu trữ lịch sử trò chuyện theo userId
    private final Map<String, List<Map<String, String>>> conversationHistoryMap = new ConcurrentHashMap<>();
    
    @Autowired
    private GeminiService geminiService;
    
    /**
     * Xử lý tin nhắn từ người dùng và trả về phản hồi
     * 
     * @param request Yêu cầu từ người dùng
     * @return Phản hồi từ chatbot
     */
    public ChatbotResponse processMessage(ChatbotRequest request) {
        String userId = request.getUserId();
        String userMessage = request.getMessage();
        List<Map<String, String>> history = request.getConversationHistory();
        
        if (userId == null || userId.trim().isEmpty()) {
            userId = "anonymous-" + System.currentTimeMillis();
        }
        
        // Lưu tin nhắn của người dùng vào lịch sử
        Map<String, String> userMessageMap = new HashMap<>();
        userMessageMap.put("role", "user");
        userMessageMap.put("content", userMessage);
        history.add(userMessageMap);
        
        // Cắt bớt lịch sử nếu quá dài
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(history.size() - MAX_HISTORY_SIZE, history.size());
        }
        
        // Gọi GeminiService để lấy phản hồi
        String responseMessage;
        String source;
        try {
            responseMessage = geminiService.generateResponse(userMessage, history);
            source = "gemini";
        } catch (Exception e) {
            LOGGER.severe("Error generating response from Gemini: " + e.getMessage());
            responseMessage = "Xin lỗi, tôi đang gặp sự cố kết nối. Vui lòng thử lại sau!";
            source = "error";
        }
        
        // Lưu phản hồi của bot vào lịch sử
        Map<String, String> botMessageMap = new HashMap<>();
        botMessageMap.put("role", "assistant");
        botMessageMap.put("content", responseMessage);
        history.add(botMessageMap);
        
        // Lưu lịch sử cho người dùng này
        conversationHistoryMap.put(userId, history);
        
        // Tạo và trả về phản hồi
        return ChatbotResponse.builder()
                .message(responseMessage)
                .userId(userId)
                .success(true)
                .conversationHistory(history)
                .source(source)
                .build();
    }
    
    /**
     * Lấy lịch sử cuộc trò chuyện của một người dùng
     * 
     * @param userId ID của người dùng
     * @return Lịch sử cuộc trò chuyện
     */
    public List<Map<String, String>> getConversationHistory(String userId) {
        return conversationHistoryMap.getOrDefault(userId, List.of());
    }
    
    /**
     * Xóa lịch sử cuộc trò chuyện của một người dùng
     * 
     * @param userId ID của người dùng
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean clearConversationHistory(String userId) {
        if (conversationHistoryMap.containsKey(userId)) {
            conversationHistoryMap.remove(userId);
            return true;
        }
        return false;
    }
}