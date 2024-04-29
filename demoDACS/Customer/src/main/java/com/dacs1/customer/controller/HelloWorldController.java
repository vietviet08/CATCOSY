package com.dacs1.customer.controller;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HelloWorldController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/hello-world")
    public String helloWorld(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("authentication", authentication);

        return "hello-world";
    }

    @GetMapping("/test-mail-register")
    public String testMailRegister(Model model, Principal principal) {

        Customer customer = customerService.findByUsername(principal.getName());

        model.addAttribute("fullName", customer.getFirstName() +" " + customer.getLastName());
        model.addAttribute("email", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());
        model.addAttribute("username", customer.getUsername());

        return "mail-registered-customer";
    }
}
