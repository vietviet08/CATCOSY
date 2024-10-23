package com.catcosy.customer.controller;

import com.catcosy.library.model.Customer;
import com.catcosy.library.service.CustomerService;
import com.catcosy.library.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MailService mailService;


    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot password");
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = UUID.randomUUID().toString();
        try {
            Customer customer = customerService.updateResetPasswordToken(email, token);

            if (customer == null) {
                model.addAttribute("error", "Email not found please login with Google or Facebook!");
                return "forgot-password";
            }
            String siteURL = request.getRequestURL().toString();
            String url = siteURL.replace(request.getServletPath(), "");
            String resetPasswordLink = url + "/reset-password?token=" + token;
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

            mailService.sendMaiResetPasswordToCustomer(email, resetPasswordLink);

        } catch (Exception ex) {

            model.addAttribute("error", "Error from server!");
            ex.printStackTrace();
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("title", "Reset password");

        if (token.equals("")) return "404";


        Customer customerDto = customerService.findByResetPasswordToken(token);

        if (customerDto == null) return "404";

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        Customer customer = customerService.findByResetPasswordToken(token);
        String finalPassword = bCryptPasswordEncoder.encode(password);

        if (customer != null) customerService.updatePassword(customer, finalPassword);

        return "redirect:/login";
    }
}
