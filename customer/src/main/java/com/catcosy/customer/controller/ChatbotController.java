package com.catcosy.customer.controller;

import com.catcosy.customer.model.ChatbotRequest;
import com.catcosy.customer.model.ChatbotResponse;
import com.catcosy.customer.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chatbot")
    public ResponseEntity<ChatbotResponse> processChatbotRequest(@RequestBody ChatbotRequest request) {
        ChatbotResponse response = chatbotService.processQuery(request);
        return ResponseEntity.ok(response);
    }
}