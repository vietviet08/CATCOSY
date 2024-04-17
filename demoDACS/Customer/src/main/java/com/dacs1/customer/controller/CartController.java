package com.dacs1.customer.controller;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Product;
import com.dacs1.library.service.CartService;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;


    @GetMapping("/cart")
    public String getToCart(Model model, Principal principal, HttpServletRequest request){
        model.addAttribute("title", "Cart");

        if(principal == null){
        //process with cookie
            Cookie cookieCart = null;

            Cookie[] cookies = request.getCookies();
            for(Cookie c : cookies) if(c.getName().equals("CART_CATCOSY")) cookieCart = c;

            if(cookieCart == null){
                model.addAttribute("notCart", "There are no products in the cart!");
            }

        }else{
            Customer customer = customerService.findByUsername(principal.getName());
            Cart cart = customer.getCart();

            if(cart == null){
                model.addAttribute("notCart", "There are no products in the cart!");
//                model.addAttribute("totalProduct", 0);
            }

            model.addAttribute("cart", cart);
        }

        return "cart";
    }


    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long id,
                            @RequestParam(value = "quantity",required = false, defaultValue = "1") int quantity,
                            Model model,
                            Principal principal,
                            HttpServletRequest request,
                            HttpServletResponse response){


        if(principal == null){
            //add product to cookie
            //process product with cookie
            Cookie[] cookies = request.getCookies();
            Cookie cookieCart = null;

            for(Cookie cookie : cookies) if(cookie.getName().equals("CART_CATCOSY"))  cookieCart = cookie;

            if(cookieCart == null){
                    //create new cookie
                    // need create cookie save product in the cart
                    // may be jwt i think so
                    Cookie cookie = new Cookie("CART_CATCOSY", "test");
                    cookie.setPath("/");
                    cookie.setMaxAge(3600 * 24 * 3);
                    response.addCookie(cookie);
            }else{

                //process cookie to get product

            }

            return "cart";
        }

        Product product = productService.getProductById(id);
        Customer customer = customerService.findByUsername(principal.getName());
        Cart cart = customer.getCart();

        cartService.addItemToCard(product,quantity,customer);

        return "redirect:" + request.getHeader("Referer");
    }

}
