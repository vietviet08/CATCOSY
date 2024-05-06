package com.dacs1.admin.controller;

import com.dacs1.admin.helper.SetNameAndRoleToPage;
import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.model.Admin;
import com.dacs1.library.model.AuthenticationRequest;
import com.dacs1.library.model.Role;
import com.dacs1.library.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class LoginController {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("authRequest", new AuthenticationRequest());
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot password");
        return "forgot-password";
    }


    @GetMapping("/register")
    public String registerForm(Model model) {
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




    @RequestMapping("/index")
    public String home(Model model, HttpSession session, Principal principal) {
        if(principal == null) return "redirect:/login";

        Admin admin =  adminService.findByUsername(principal.getName());
        session.setAttribute("nameAdmin",admin.getFirstName() + " " + admin.getLastName());
        session.setAttribute("role", "ADMIN");
        model.addAttribute("title", "Home Page");

        return "index";
    }


}
