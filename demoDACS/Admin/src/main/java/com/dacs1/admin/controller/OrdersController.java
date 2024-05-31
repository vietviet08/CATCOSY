package com.dacs1.admin.controller;

import com.dacs1.admin.utils.ExcelExporter;
import com.dacs1.library.dto.OrderDetailDto;
import com.dacs1.library.dto.OrderDto;
import com.dacs1.library.enums.ObjectManage;
import com.dacs1.library.model.Order;
import com.dacs1.library.service.OrderDetailService;
import com.dacs1.library.service.OrderService;
import jakarta.persistence.Column;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @GetMapping("/export-orders")
    public void exportProduct(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=orders_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        ExcelExporter excelExporter = new ExcelExporter(orderService.findAllOrder());


        List<String> fieldsToExport = List.of("id",
                "orderDate",
                "deliveryDate",
                "totalPrice",
                "discountPrice",
                "shippingFee",
                "deliveryAddress",
                "paymentMethod",
                "status",
                "notes",
                "isAccept",
                "isCancel");
        excelExporter.export(response, ObjectManage.Orders.name(), fieldsToExport);
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
    public OrderDto statusOrder(Long id) {
        return orderService.findOrderByIdDto(id);
    }

    @PostMapping("/save-change-status")
    public String saveChangeStatus(@RequestParam("idOrderNeed") Long idOrderNeed,
                                   @RequestParam("status") int status,
                                   @RequestParam("notes") String notes) {


        orderService.changeStatusAndNote(idOrderNeed, status, notes);

        return "redirect:/orders";
    }

}
