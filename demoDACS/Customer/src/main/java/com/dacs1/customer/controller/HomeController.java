package com.dacs1.customer.controller;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.dto.ProductDto;
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

        if(principal != null){
            Customer customer = customerService.findByUsername(principal.getName());
            session.setAttribute("nameForCustomer", customer.getFirstName() + " " + customer.getLastName());
            session.setAttribute("logged", true);
            //process with cookie

            Cookie[] cookies = request.getCookies();
            boolean checkExistCookie = false;
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("CART_CATCOSY")) checkExistCookie = true;
            }
            if(!checkExistCookie){
                //create new cookie
                // need create cookie save product in the cart
                // may be jwt i think so
                Cookie cookie = new Cookie("CART_CATCOSY", "test");
                cookie.setPath("/");
                cookie.setMaxAge(3600 * 24 * 3);
                response.addCookie(cookie);

            }else{

                //process cookie for get product add to cart

            }


        }else
            session.setAttribute("logged", false);

        List<ProductDto> productsNewArrival = productService.productRandomLimit(4);
        List<ProductDto> productsSale = productService.productRandomLimit(4);
        model.addAttribute("title", "Catcosy - Home");
        model.addAttribute("productsSale", productsSale);
        model.addAttribute("productsNewArrival", productsNewArrival);
        return "index";
    }

}
