package com.dacs1.customer.controller;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping({"/", "/shop"})
    public String getHomeShop(Model model) {
        List<ProductDto> productsNewArrival = productService.productRandomLimit(4);
        List<ProductDto> productsSale = productService.productRandomLimit(4);
        model.addAttribute("title", "Catcosy - Home");
        model.addAttribute("productsSale", productsSale);
        model.addAttribute("productsNewArrival", productsNewArrival);
        return "index";
    }

}
