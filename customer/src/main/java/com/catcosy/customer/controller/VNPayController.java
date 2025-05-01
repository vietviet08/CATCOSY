package com.catcosy.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.catcosy.customer.service.VNPayService; 

@Controller
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;

    @GetMapping("/vnp/test")
    public String home(){
        return "vnp-test";
    }

    @PostMapping("/vnp/submitOrder")
    public String submidOrder(
            @RequestParam("amount") int orderTotal,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    // @GetMapping("/vnp/vnpay-payment")
    // public String GetMapping(HttpServletRequest request, Model model) {
    //     int paymentStatus = vnPayService.orderReturn(request);

    //     String orderInfo = request.getParameter("vnp_OrderInfo");
    //     String paymentTime = request.getParameter("vnp_PayDate");
    //     String transactionId = request.getParameter("vnp_TransactionNo");
    //     String totalPrice = request.getParameter("vnp_Amount");

    //     model.addAttribute("orderId", orderInfo);
    //     model.addAttribute("totalPrice", totalPrice);
    //     model.addAttribute("paymentTime", paymentTime);
    //     model.addAttribute("transactionId", transactionId);

    //     return paymentStatus == 1 ? "vnp-success" : "vnp-fail";
    // }
}
