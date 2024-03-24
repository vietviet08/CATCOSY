package com.dacs1.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping({"/login", "/register"})
    public String loginPage(Model model){
        model.addAttribute("title", "Login or Register");
        return "login_register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model){
        model.addAttribute("title", "Forgot password");
        return "forgot-password";
    }




}
