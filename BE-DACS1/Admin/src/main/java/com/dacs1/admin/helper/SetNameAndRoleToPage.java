package com.dacs1.admin.helper;

import com.dacs1.library.model.Admin;
import com.dacs1.library.service.AdminService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.util.stream.Collectors;

public class SetNameAndRoleToPage {

    public static String setNameAndRoleToPage(Model model, String page, AdminService adminService) {
//        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Admin admin = adminService.findByUsername(username);
            String roles = admin.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.joining(", "));

            model.addAttribute("name", admin.getFirstName() + " " + admin.getLastName());
            model.addAttribute("role", roles);
            return page;
        }
    }
}
