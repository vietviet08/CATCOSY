package com.dacs1.customer.controller;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private ProductService productService;


    @GetMapping("*")
    public String errorPage(Model model){
        model.addAttribute("title", "Error page");
        return "404";
    }




}
