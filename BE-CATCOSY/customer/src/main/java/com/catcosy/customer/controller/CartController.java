package com.catcosy.customer.controller;

import com.catcosy.library.model.*;
import com.catcosy.library.service.CartService;
import com.catcosy.library.service.CustomerService;
import com.catcosy.library.service.ProductService;
import com.catcosy.library.service.SizeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

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
            if (cookies != null)
                for (Cookie c : cookies) if (c.getName().equals("CART_CATCOSY")) cookieCart = c;

            if (cookieCart == null) {
                model.addAttribute("notCart", "There are no products in the cart!");
            }

        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer != null) {
                Cart cart = customer.getCart();

                if (cart == null || cart.getTotalItem() == 0) {
                    model.addAttribute("notCart", "There are no products in the cart!");
//                model.addAttribute("totalProduct", 0);
                } else {
                    Set<CartItem> products = cart.getItems();


                    model.addAttribute("products", products);

                    model.addAttribute("cart", cart);
                }
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
    public String deleteCartItem(@RequestParam(value = "idProduct") Long idProduct,
                                 Principal principal) {

        Product product = productService.getProductById(idProduct);
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


    @GetMapping("/cart-v0")
    public ResponseEntity<Set<CartItem>> findAll(Principal principal) {
        return new ResponseEntity<>(customerService.findByUsername("vietnq123").getCart().getItems(), HttpStatus.OK);
    }

    @PostMapping("/update-cart")
    public ResponseEntity updateCart(@RequestBody CartItem cartItem,
                                     @RequestParam("up") boolean up,
                                     Principal principal) {

        Product product = cartItem.getProduct();
        Size size = cartItem.getSize();
        int quantity = cartItem.getQuantity();
        Customer customer = customerService.findByUsername(principal.getName());

        if (up) cartService.addItemToCard(product, quantity, size, customer);
        else cartService.updateCartItem(product, quantity, size, customer);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
