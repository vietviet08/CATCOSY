package com.dacs1.customer.controller;

import com.dacs1.library.model.*;
import com.dacs1.library.service.CartService;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.ProductService;
import com.dacs1.library.service.SizeService;
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
import java.util.Set;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SizeService sizeService;


    @GetMapping("/cart")
    public String getToCart(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("title", "Cart");

        if (principal == null) {
            //process with cookie
            Cookie cookieCart = null;

            Cookie[] cookies = request.getCookies();
            for (Cookie c : cookies) if (c.getName().equals("CART_CATCOSY")) cookieCart = c;

            if (cookieCart == null) {
                model.addAttribute("notCart", "There are no products in the cart!");
            }

        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            Cart cart = customer.getCart();

            if (cart == null) {
                model.addAttribute("notCart", "There are no products in the cart!");
//                model.addAttribute("totalProduct", 0);
            } else {
                Set<CartItem> products = cart.getItems();
                model.addAttribute("products", products);
                model.addAttribute("cart", cart);
            }


        }

        return "cart";
    }


    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                            @RequestParam(value = "sizeId", required = false) Long sizeId,
                            Model model,
                            Principal principal,
                            HttpServletRequest request,
                            HttpServletResponse response) {


        if (principal == null) {
            //add product to cookie
            //process product with cookie
            Cookie[] cookies = request.getCookies();
            Cookie cookieCart = null;

            for (Cookie cookie : cookies) if (cookie.getName().equals("CART_CATCOSY")) cookieCart = cookie;

            if (cookieCart == null) {
                //create new cookie
                // need create cookie save product in the cart
                // may be jwt i think so
                Cookie cookie = new Cookie("CART_CATCOSY", "test");
                cookie.setPath("/");
                cookie.setMaxAge(3600 * 24 * 3);
                response.addCookie(cookie);
            } else {

                //process cookie to get product

            }

            return "cart";
        }

        Product product = productService.getProductById(id);
        Size size = sizeService.getSizeById(sizeId);
        Customer customer = customerService.findByUsername(principal.getName());
        cartService.addItemToCard(product, quantity, size, customer);

        return "redirect:" + request.getHeader("Referer");
    }


    @PostMapping("/delete-cart-item")
    public String deleteCartItem(@RequestParam("id") Long id, Principal principal) {

        Product product = productService.getProductById(id);
        Customer customer = customerService.findByUsername(principal.getName());
        cartService.deleteCartItem(product, customer);

        return "redirect:/cart";
    }

    @PostMapping("/update-cart-item")
    public String updateCartItem(@RequestParam("id") Long id,
                                 @RequestParam("quantity") int quantity,
                                 @RequestParam("sizeId") Long sizeId,
                                 Principal principal) {

        Product product = productService.getProductById(id);
        Customer customer = customerService.findByUsername(principal.getName());
        Size size = sizeService.getSizeById(id);

        cartService.updateCartItem(product, quantity, size, customer);


        return "redirect:/cart";
    }

}
