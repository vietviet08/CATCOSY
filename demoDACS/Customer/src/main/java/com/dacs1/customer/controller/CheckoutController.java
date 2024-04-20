package com.dacs1.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class CheckoutController {


    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal){


        if(principal == null) return "login_register";



        return "checkout";
    }

}
