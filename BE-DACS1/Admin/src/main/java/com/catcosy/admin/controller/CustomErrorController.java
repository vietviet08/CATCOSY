package com.dacs1.admin.controller;

import com.dacs1.admin.helper.SetNameAndRoleToPage;
import com.dacs1.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @Autowired
    private AdminService adminService;

    @GetMapping("*")
    public String errorPage(Model model){
        model.addAttribute("title", "Error Page");
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "404", adminService);
        return "404";
    }
}
