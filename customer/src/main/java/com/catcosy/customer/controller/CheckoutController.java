package com.catcosy.customer.controller;

import com.catcosy.customer.service.VNPayService;
import com.catcosy.library.model.Cart;
import com.catcosy.library.model.CartItem;
import com.catcosy.library.model.Customer;
import com.catcosy.library.model.Order;
import com.catcosy.library.service.CustomerService;
import com.catcosy.library.service.MailService;
import com.catcosy.library.service.OrderService;
import com.catcosy.library.service.VoucherService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
public class CheckoutController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MailService mailService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VNPayService vnPayService;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal) {

        model.addAttribute("title", "Checkout");

        if (principal == null)
            return "redirect:/login";
        Customer customer = customerService.findByUsername(principal.getName());
        Cart cart = customer.getCart();

        if (cart == null || cart.getTotalItem() == 0)
            return "index";

        Set<CartItem> items = cart.getItems();
        double totalPrice = cart.getTotalPrice();

        String[] paymentMethod = { "Cash", "VN Pay", "Transfer" };
        model.addAttribute("paymentMethod", paymentMethod);

        String addressDetail = customer.getAddressDetail();
        if (addressDetail != null) {
            String[] address = addressDetail.split(" - ");

            if (address.length > 1) {
                String addressSelect = address[1];
                String[] childAddress = addressSelect.split(", ");

                model.addAttribute("citySelect", childAddress[0]);
                model.addAttribute("districtSelect", childAddress[1]);
                model.addAttribute("communeSelect", childAddress[2]);
            }
            model.addAttribute("address", address[0]);
        }

        model.addAttribute("products", items);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("customer", customer);
        model.addAttribute("cart", cart);
        model.addAttribute("orderInfo", new Order());

        return "checkout";
    }

    @RequestMapping(value = "/checkout/check-voucher", method = { RequestMethod.GET, RequestMethod.PUT })
    @ResponseBody
    public String checkCartToVoucher(@RequestParam("codeVoucher") String codeVoucher,
            @RequestParam("idCart") Long id,
            Model model) {
        try {
            model.addAttribute("success", "Apply voucher successfully!");
            double voucherAmount = voucherService.checkCartToApplyVoucher(codeVoucher, id);
            return String.valueOf(voucherAmount);
        } catch (Exception e) {
            model.addAttribute("error", "Voucher not exist!");
            e.printStackTrace();
            return "0.0";
        }
    }

    @PostMapping("/process-checkout")
    public String processCheckout(@ModelAttribute("orderInfo") Order order,
            @RequestParam("codeVoucherCompletePayment") String codeVoucherCompletePayment,
            Model model,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null)
            return "redirect:/login";
        try {
            Customer customer = customerService.findByUsername(principal.getName());
            Cart cart = customer.getCart();
            
            if (order.getPaymentMethod().equals("VN Pay")) {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String vnpayUrl = vnPayService.createOrder(cart.getTotalPrice().intValue(), order.getDeliveryAddress(),
                        baseUrl);
                if (codeVoucherCompletePayment != null && !codeVoucherCompletePayment.isEmpty()) {
                    order.setVoucherCode(codeVoucherCompletePayment);
                }
                orderService.saveTemporaryOrder(order, cart);
                return "redirect:" + vnpayUrl;
            } else {
                Order orderAdded = orderService.addOrder(cart, order);
                if (codeVoucherCompletePayment != null && !codeVoucherCompletePayment.isEmpty()) {
                    voucherService.applyVoucher(codeVoucherCompletePayment, orderAdded.getId());
                }
                mailService.sendMailOrderToCustomer(customer, orderAdded);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xử lý thanh toán: " + e.getMessage());
            return "checkout-error";
        }
        return "redirect:/orders";
    }

    @GetMapping("/vnp/vnpay-payment")
    public String handleVNPayPayment(HttpServletRequest request, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        if (paymentStatus == 1 && orderInfo != null) {
            try {
                Order order = orderService.finalizeOrder(orderInfo);
                
                if (order != null) {
                    if (order.getVoucherCode() != null && !order.getVoucherCode().isEmpty()) {
                        voucherService.applyVoucher(order.getVoucherCode(), order.getId());
                    }
                    mailService.sendMailOrderToCustomer(order.getCustomer(), order);
                    return "vnp-success";
                }
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Lỗi xử lý thanh toán: " + e.getMessage());
            }
        } else {
            model.addAttribute("errorMessage", "Thanh toán thất bại hoặc bị hủy");
        }
        
        return "vnp-fail";
    }

}
