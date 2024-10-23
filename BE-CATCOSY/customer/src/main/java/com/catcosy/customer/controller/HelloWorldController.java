package com.catcosy.customer.controller;

import com.catcosy.library.model.Customer;
import com.catcosy.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class HelloWorldController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/hello-world")
    public String helloWorld(Model model, @AuthenticationPrincipal OAuth2User oAuth2User,
            Authentication authentication) {

        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User1 = (OAuth2User) authentication.getPrincipal();

        model.addAttribute("authentication", authentication);

        Map<String, Object> attribute = oAuth2User.getAttributes();

        return "hello-world";
    }

    @GetMapping("/test-mail-register")
    public String testMailRegister(Model model, Principal principal) {

        Customer customer = customerService.findByUsername(principal.getName());

        model.addAttribute("fullName", customer.getFirstName() + " " + customer.getLastName());
        model.addAttribute("email", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());
        model.addAttribute("username", customer.getUsername());

        return "mail-registered-customer";
    }
}
