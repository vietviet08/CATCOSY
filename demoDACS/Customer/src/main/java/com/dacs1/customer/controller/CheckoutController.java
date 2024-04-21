package com.dacs1.customer.controller;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.Customer;
import com.dacs1.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CheckoutController {

    @Autowired
    private CustomerService customerService;


    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal){

        model.addAttribute("title", "Checkout");

        if(principal == null) return "redirect:/login";
        Customer customer = customerService.findByUsername(principal.getName());
        Cart cart = customer.getCart();

        if(cart == null || cart.getTotalItem() == 0) return "index";

        return "checkout";
    }


    @PostMapping("/process-checkout")
    public String processCheckout(){



        return "redirect:/home";
    }

}
