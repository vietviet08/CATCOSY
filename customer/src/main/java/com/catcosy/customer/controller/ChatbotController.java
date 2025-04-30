package com.catcosy.customer.controller;

import com.catcosy.customer.model.ChatbotRequest;
import com.catcosy.customer.model.ChatbotResponse;
import com.catcosy.customer.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class ChatbotController {

    private static final Logger LOGGER = Logger.getLogger(ChatbotController.class.getName());
    
    @Autowired
    private ChatbotService chatbotService;
    
    /**
     * API endpoint để gửi tin nhắn đến chatbot và nhận phản hồi
     * 
     * @param request Yêu cầu từ người dùng
     * @return Phản hồi từ chatbot
     */
    @PostMapping("/send")
    public ResponseEntity<ChatbotResponse> sendMessage(@RequestBody ChatbotRequest request) {
        LOGGER.info("Received message request: " + request.getMessage());
        ChatbotResponse response = chatbotService.processMessage(request);
        LOGGER.info("Sending response: " + response.getMessage());
        return ResponseEntity.ok(response);
    }
    
    /**
     * API endpoint để lấy lịch sử cuộc trò chuyện của một người dùng
     * 
     * @param userId ID của người dùng
     * @return Lịch sử cuộc trò chuyện
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getConversationHistory(@PathVariable String userId) {
        LOGGER.info("Fetching conversation history for user: " + userId);
        List<Map<String, String>> history = chatbotService.getConversationHistory(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("history", history);
        response.put("count", history.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API endpoint để xóa lịch sử cuộc trò chuyện của một người dùng
     * 
     * @param userId ID của người dùng
     * @return Kết quả xóa
     */
    @DeleteMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> clearConversationHistory(@PathVariable String userId) {
        LOGGER.info("Clearing conversation history for user: " + userId);
        boolean success = chatbotService.clearConversationHistory(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("success", success);
        response.put("message", success ? "Đã xóa lịch sử cuộc trò chuyện" : "Không tìm thấy lịch sử cuộc trò chuyện");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API endpoint để kiểm tra sức khỏe của chatbot
     * 
     * @return Trạng thái sức khỏe
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        LOGGER.info("Health check endpoint called");
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Chatbot");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * API endpoint test đơn giản để kiểm tra controller đã được đăng ký
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        LOGGER.info("Test endpoint called");
        return ResponseEntity.ok("Chatbot API is working!");
    }
}