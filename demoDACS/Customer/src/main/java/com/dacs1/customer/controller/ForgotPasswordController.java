package com.dacs1.customer.controller;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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

    @PostMapping("/forgot-password-post")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = UUID.randomUUID().toString();
        System.out.println(token);
        try {
            customerService.updateResetPasswordToken(email, token);
            String siteURL = request.getRequestURL().toString();
            String url = siteURL.replace(request.getServletPath(), "");
            String resetPasswordLink = url + "/reset-password?token=" + token;
            mailService.sendMaiResetPasswordToCustomer(email, resetPasswordLink);

            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            ex.printStackTrace();
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("title", "Reset password");

        if (token == null) return "404";

        CustomerDto customerDto = customerService.findByResetPasswordToken(token);

        if (customerDto == null) return "404";


        return "reset-password";
    }

    @PostMapping("/reset-password-post")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        CustomerDto customerDto = customerService.findByResetPasswordToken(token);
        String finalPassword = bCryptPasswordEncoder.encode(password);

        if (customerDto != null) customerService.updatePassword(customerDto, finalPassword);

        return "login_register";
    }
}
