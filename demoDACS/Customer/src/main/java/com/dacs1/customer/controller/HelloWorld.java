package com.dacs1.customer.controller;

import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorld {

    @GetMapping("/hello-world")
    public String helloWorld(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("authentication", authentication);

        return "hello-world";
    }
}
