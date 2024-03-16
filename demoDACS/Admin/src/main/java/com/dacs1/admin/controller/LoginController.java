package com.dacs1.admin.controller;

import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.model.Admin;
import com.dacs1.library.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String loginForm(Model model)
    {
        model.addAttribute("title", "Login");
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot password");
        return "forgot-password";
    }

    @RequestMapping("/index")
    public String home(Model model) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("title", "Register");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                return "register";
            }

            Admin admin = adminService.findByUsername(adminDto.getUsername());
            if (admin != null) {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("error_email", "Your email has been register!");
                return "register";
            }

            if (!adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("error_password", "Password not correct!");
                return "register";
            }

            adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
            adminService.save(adminDto);
            model.addAttribute("adminDto", adminDto);
            model.addAttribute("success", "Register successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error register from server!");
        }
        return "register";
    }




}
