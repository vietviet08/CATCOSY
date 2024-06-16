package com.catcosy.customer.controller;

import com.catcosy.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    @Autowired
    private ProductService productService;


    @GetMapping("*")
    public String errorPage(Model model){
        model.addAttribute("title", "Error page");
        return "404";
    }

    @GetMapping("/404")
    public String page404(Model model){
        model.addAttribute("title", "Error page");
        return "404";
    }




}
