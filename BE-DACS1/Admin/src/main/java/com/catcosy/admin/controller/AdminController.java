package com.dacs1.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/")
    public String toIndex() {
        return "redirect:/index";
    }

    @GetMapping("/error-500")
    public String error500() {
        return "500";
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}
