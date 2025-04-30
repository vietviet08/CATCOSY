package com.catcosy.customer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.catcosy.customer.model.ChatMessage;
import com.catcosy.customer.service.GeminiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatbotController {

    private final GeminiService geminiService;

    @GetMapping("/chatbot")
    public String showChatbot(Model model) {
        model.addAttribute("messages", new ArrayList<ChatMessage>());
        return "chatbot";
    }

    @PostMapping("/chatbot/send")
    @ResponseBody
    public ChatMessage sendMessage(@RequestBody ChatMessage userMessage) {
        try {
            String botResponse = geminiService.getResponse(userMessage.getContent());
            ChatMessage response = new ChatMessage();
            response.setSender("Bot");
            response.setContent(botResponse);
            return response;
        } catch (Exception e) {
            ChatMessage errorResponse = new ChatMessage();
            errorResponse.setSender("Bot");
            errorResponse.setContent("Sorry, something went wrong. Please try again.");
            return errorResponse;
        }
    }

    @GetMapping("/chatbot/check-api-key")
    @ResponseBody
    public String checkApiKey() {
        return geminiService.checkApiKey();
    }
}