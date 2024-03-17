package com.dacs1.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    @GetMapping("/products")
    public String productPage(Model model){
        model.addAttribute("title", "Products");

        return "products";
    }


}
