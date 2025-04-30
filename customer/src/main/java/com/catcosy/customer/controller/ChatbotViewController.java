package com.catcosy.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller để hiển thị giao diện người dùng của chatbot
 */
@Controller
public class ChatbotViewController {

    /**
     * Hiển thị trang chatbot HTML
     * @return tên template
     */
    @GetMapping("/chatbot")
    public String showChatbotPage() {
        return "chatbot";
    }
}