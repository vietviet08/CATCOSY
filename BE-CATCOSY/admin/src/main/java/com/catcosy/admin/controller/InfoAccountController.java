package com.catcosy.admin.controller;

import com.catcosy.library.dto.AdminDto;
import com.catcosy.library.model.Admin;
import com.catcosy.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class InfoAccountController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/my-info")
    public String myInfo(Principal principal, Model model) {
        if (principal == null) return "/login";

        model.addAttribute("title", "Your info");

        Admin admin = adminService.findByUsername(principal.getName());

        model.addAttribute("id", admin.getId());
        model.addAttribute("username", admin.getUsername());
        model.addAttribute("firstName", admin.getFirstName());
        model.addAttribute("lastName", admin.getLastName());
        model.addAttribute("email", admin.getEmail());
        model.addAttribute("phone", admin.getPhone());
        model.addAttribute("imageAvatar", admin.getImage());

        return "info";
    }

    @PostMapping("/save-my-info")
    public String saveMyInfo(@RequestParam("id") Long id,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phone,
                             @RequestParam("avatarImage") MultipartFile image,
                             RedirectAttributes model) {

        try {

            AdminDto admin = new AdminDto();
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setEmail(email);
            admin.setPhone(phone);
            adminService.update(admin, id, image);

            model.addFlashAttribute("success", "Update your information successfully!");
        } catch (Exception e) {
            model.addFlashAttribute("error", "Update your information fail, maybe error from server!");
            e.printStackTrace();
        }
        return "redirect:/my-info";
    }

    @GetMapping("/change-password")
    public String changePassword(Principal principal, Model model) {

        model.addAttribute("title", "Change password");
        model.addAttribute("id", adminService.findByUsername(principal.getName()).getId());

        return "info-change-password";
    }

    @PostMapping("/save-change-password")
    public String saveChangePassword(@RequestParam("id") Long id,
                                     @RequestParam("currentPassword") String currentPassword,
                                     @RequestParam("newPassword") String newPassword,
                                     RedirectAttributes attributes) {

        try {
            Admin admin = adminService.saveChangePassword(id, currentPassword, newPassword);

            if (admin == null) attributes.addFlashAttribute("warning", "Your password not correct!");

            else attributes.addFlashAttribute("success", "Update your information successfully!");

        } catch (UsernameNotFoundException ex) {
            attributes.addFlashAttribute("warning", "Your password not correct!");
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Update your password fail, maybe error from server!");
        }

        return "redirect:/change-password";
    }
}
