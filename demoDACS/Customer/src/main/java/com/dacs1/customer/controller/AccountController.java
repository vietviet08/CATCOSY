package com.dacs1.customer.controller;

import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class AccountController {


    @Autowired
    private CustomerService customerService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private OrderService orderService;

    @GetMapping("/account")
    public String myAccount(Principal principal, Model model) {

        if (principal == null) return "redirect:/login";
        model.addAttribute("title", "Account");

        Customer customer = customerService.findByUsername(principal.getName());

        model.addAttribute("nameUser", customer.getFirstName() + " " + customer.getLastName());
        model.addAttribute("firstName", customer.getFirstName());
        model.addAttribute("lastName", customer.getLastName());
        model.addAttribute("emailUser", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());

        return "account";
    }


    @PostMapping("/account")
    public String changeInfo(@RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("phone") String phone,
                             Model model,
                             Principal principal) {

        Customer customer = customerService.findByUsername(principal.getName());


        try {

            if (firstName.trim().equals("") || lastName.trim().equals("")) {
                model.addAttribute("notAccept", "First name or last name not empty");
                return "account";
            } else {

                customer.setFirstName(firstName);
                customer.setLastName(lastName);
                customer.setPhone(phone);
                customerService.updateCustomer(customer);
                model.addAttribute("success", "Change info successfully!");
                return "redirect:/account";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error from server!");
        }


        return "account";
    }


    @GetMapping("/address")
    public String myAddress(Principal principal, Model model) {

        if (principal == null) return "redirect:/login";
        model.addAttribute("title", "Address");

        Customer customer = customerService.findByUsername(principal.getName());

        model.addAttribute("nameUser", customer.getFirstName() + " " + customer.getLastName());
        model.addAttribute("emailUser", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());
        model.addAttribute("address", customer.getAddressDetail());

        return "account-address";
    }


    /*Orders*/
    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {

        if (principal == null) return "redirect:/login";
        model.addAttribute("title", "Orders");

        List<Order> orders = orderService.finAllOrderByCustomerId(customerService.findByUsername(principal.getName()));

        model.addAttribute("orders", orders);

        return "account-orders";
    }

    @RequestMapping(value = "/cancel-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String cancelOrder(Order order, Principal principal, Model model) {
        if(principal == null) return "redirect:/404";

        if(order.isAccept()) return "redirect:/orders";

        orderService.cancelOrderForCustomer(order.getId());
        model.addAttribute("success", "Accept order successfully!");

        return "redirect:/orders";
    }


    @GetMapping("/change-password")
    public String changePassword(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("title", "Orders");

        return "account-password";
    }

    @PostMapping(value = "/do-change-password")
    public String doChangePassword(@RequestParam("oldPassword") String oldPassword,
                                   @RequestParam("password") String password,
                                   @RequestParam("repeatPassword") String repeatPassword,
                                   Model model,
                                   Principal principal) {
        try {

            Customer customer = customerService.findByUsername(principal.getName());
            if (passwordEncoder.matches(oldPassword, customer.getPassword())) {
                if (password.length() <= 3) model.addAttribute("notLength", "Password has at least 4 characters!");
                else if (password.equals(repeatPassword)) {
                    customer.setPassword(passwordEncoder.encode(password));
                    customerService.updateCustomer(customer);
                    model.addAttribute("success", "Change password successfully!");
                } else {
                    model.addAttribute("notSame", "Please enter a matching password!");
                    return "account-password";
                }
            } else {
                model.addAttribute("notCorrect", "Please enter the correct password!");
                return "account-password";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error from server");
        }

        return "account-password";
    }
}
