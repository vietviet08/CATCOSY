package com.dacs1.customer.controller;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.CartItem;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.MailService;
import com.dacs1.library.service.OrderService;
import com.dacs1.library.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.JMoleculesConverters;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
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

    @RequestMapping(value = "/checkout/check-voucher", method = {RequestMethod.GET, RequestMethod.PUT})
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
                                  Model model, Principal principal) {

        if (principal == null) return "redirect:/login_register";
        try {
            Order orderAdded = orderService.addOrder(customerService.findByUsername(principal.getName()).getCart(), order);
            voucherService.applyVoucher(codeVoucherCompletePayment, orderAdded.getId());
            mailService.sendMailOrderToCustomer(customerService.findByUsername(principal.getName()), order);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/orders";
    }

}
