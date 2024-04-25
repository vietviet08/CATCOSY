package com.dacs1.admin.controller;

import com.dacs1.library.model.Order;
import com.dacs1.library.service.OrderService;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String ordersPage(Model model){

        List<Order> orders = orderService.findAllOrder();

        model.addAttribute("orders", orders);
        model.addAttribute("title", "Orders");

        return "orders";
    }


    @RequestMapping(value = "/accept-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String acceptOrder(Order order, Model model){
        orderService.acceptOrder(order.getId());
        model.addAttribute("success", "Accept order successfully!");

        return "redirect:/orders";
    }

    @RequestMapping(value = "/cancel-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String cancelOrder(Order order, Model model){
        orderService.cancelOrder(order.getId());
        model.addAttribute("success", "Accept order successfully!");

        return "redirect:/orders";
    }


    @RequestMapping(value = "/delete-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String deleteOrder(Order order, Model model){

        orderService.deleteOrder(order.getId());
        model.addAttribute("success", "Delete order successfully!");

        return "redirect:/orders";
    }

}
