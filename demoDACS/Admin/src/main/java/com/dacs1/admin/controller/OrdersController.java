package com.dacs1.admin.controller;

import com.dacs1.library.dto.OrderDetailDto;
import com.dacs1.library.model.Order;
import com.dacs1.library.service.OrderDetailService;
import com.dacs1.library.service.OrderService;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/orders")
    public String ordersPage(Model model) {

        List<Order> orders = orderService.findAllOrderByIdDesc();

        model.addAttribute("orders", orders);
        model.addAttribute("title", "Orders");

        return "orders";
    }


    @RequestMapping(value = "/accept-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String acceptOrder(Order order, Model model) {
        orderService.acceptOrder(order.getId());
        model.addAttribute("success", "Accept order successfully!");

        return "redirect:/orders";
    }

    @RequestMapping(value = "/cancel-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String cancelOrder(Order order, Model model) {
        orderService.cancelOrder(order.getId());
        model.addAttribute("success", "Accept order successfully!");

        return "redirect:/orders";
    }


    @RequestMapping(value = "/detail-order", method = {RequestMethod.GET})
    @ResponseBody
    public List<OrderDetailDto> viewOrderDetail(Long id, Model model) {
        return orderDetailService.finAllByOrderIdDto(id);
    }

    @RequestMapping(value = "/delete-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String deleteOrder(Order order, Model model) {

        orderService.deleteOrder(order.getId());
        model.addAttribute("success", "Delete order successfully!");

        return "redirect:/orders";
    }


    @RequestMapping(value = "/status-order", method = {RequestMethod.GET, RequestMethod.PUT})
    @ResponseBody
    public Order statusOrder(Long id) {
        return orderService.findOrderById(id);
    }

    @PostMapping("/save-change-status")
    public String saveChangeStatus(@RequestParam("idOrderNeed") Long idOrderNeed,
                                   @RequestParam("status") int status,
                                   @RequestParam("notes") String notes) {


        orderService.changeStatusAndNote(idOrderNeed, status, notes);

        return "redirect:/orders";
    }

}
