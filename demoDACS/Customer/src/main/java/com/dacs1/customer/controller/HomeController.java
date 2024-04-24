package com.dacs1.customer.controller;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Cart;
import com.dacs1.library.model.Customer;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.ProductService;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @GetMapping({"/", "/shop"})
    public String getHomeShop(Model model,
                              Principal principal,
                              HttpSession session,
                              HttpServletRequest request,
                              HttpServletResponse response) {
//
//        if(principal != null){
//            Customer customer = customerService.findByUsername(principal.getName());
//            session.setAttribute("nameForCustomer", customer.getFirstName() + " " + customer.getLastName());
//            session.setAttribute("logged", true);
//
//            Cart cart = customer.getCart();
//            if(cart == null) session.setAttribute("totalProduct", 0);
//            else session.setAttribute("totalProduct", cart.getTotalItem());
//
//        }else
//            session.setAttribute("logged", false);

        List<ProductDto> productsNewArrival = productService.productRandomLimit(8);
        List<ProductDto> productsSale = productService.productRandomLimit(8);
        model.addAttribute("title", "Catcosy - Home");
        model.addAttribute("productsSale", productsSale);
        model.addAttribute("productsNewArrival", productsNewArrival);
        return "index";
    }

}
