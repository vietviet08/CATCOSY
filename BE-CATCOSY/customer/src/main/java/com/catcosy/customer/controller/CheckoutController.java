package com.catcosy.customer.controller;

import com.catcosy.library.model.Cart;
import com.catcosy.library.model.CartItem;
import com.catcosy.library.model.Customer;
import com.catcosy.library.model.Order;
import com.catcosy.library.service.CustomerService;
import com.catcosy.library.service.MailService;
import com.catcosy.library.service.OrderService;
import com.catcosy.library.service.VoucherService;
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
