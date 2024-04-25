package com.dacs1.customer.controller;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.CartItem;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.JMoleculesConverters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.text.ParseException;
import java.util.Set;

@Controller
public class CheckoutController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;


    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal) {

        model.addAttribute("title", "Checkout");

        if (principal == null) return "redirect:/login";
        Customer customer = customerService.findByUsername(principal.getName());
        Cart cart = customer.getCart();

        if (cart == null || cart.getTotalItem() == 0) return "index";

        Set<CartItem> items = cart.getItems();
        double totalPrice = cart.getTotalPrice();

        String[] paymentMethod = {"Cash", "Transfer"};
        model.addAttribute("paymentMethod", paymentMethod);


        model.addAttribute("products", items);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("customer", customer);
        model.addAttribute("cart", cart);
        model.addAttribute("orderInfo", new Order());

        return "checkout";
    }


    @PostMapping("/process-checkout")
    public String processCheckout(@ModelAttribute("orderInfo") Order order, Model model, Principal principal) {

        if (principal == null) return "redirect:/login_register";
        try {
            orderService.addOrder(customerService.findByUsername(principal.getName()).getCart(), order);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/orders";
    }

}
