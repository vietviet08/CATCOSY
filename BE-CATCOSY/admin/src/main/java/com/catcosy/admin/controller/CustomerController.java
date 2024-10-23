package com.catcosy.admin.controller;

import com.catcosy.admin.utils.ExcelExporter;
import com.catcosy.library.dto.CustomerDto;
import com.catcosy.library.enums.ObjectManage;
import com.catcosy.library.model.Customer;
import com.catcosy.library.service.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class CustomerController {


    @Autowired
    private CustomerService customerService;


    @GetMapping("/customers")
    public String listAllCustomer(Model model) {
        List<CustomerDto> customerDtoList = customerService.getAllCustomer();
        model.addAttribute("title", "Customers");
        model.addAttribute("customers", customerDtoList);

        return "customer";
    }


    @GetMapping("/export-customer")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        ExcelExporter excelExporter = new ExcelExporter(customerService.getAll());


        List<String> fieldsToExport = List.of("id",
                "firstName",
                "lastName",
                "username",
                "sex",
                "addressDetail",
                "email",
                "phone",
                "provider",
                "isActive");
        excelExporter.export(response, ObjectManage.Customers.name(), fieldsToExport);
    }

    @RequestMapping(value = "/find-customer", method = {RequestMethod.GET, RequestMethod.PUT})
    @ResponseBody
    public CustomerDto findCustomer(String username) {
        return customerService.findByUsernameDto(username);
    }

    @PostMapping("/lock-customer")
    public String lockCustomer(@RequestParam("usernameCustomerNeedLock") String usernameCustomerNeed, Model model) {
        customerService.lockCustomer(usernameCustomerNeed);
        model.addAttribute("success", "Lock customer successfully");
        return "redirect:/customers";
    }

    @PostMapping("/unlock-customer")
    public String unlockCustomer(@RequestParam("usernameCustomer") String username, Model model) {
        customerService.unlockCustomer(username);
        model.addAttribute("success", "Unlock customer successfully");
        return "redirect:/customers";
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@RequestParam("usernameCustomerNeedDelete") String usernameCustomerNeed, Model model) {
        Customer customer = customerService.deleteCustomer(usernameCustomerNeed);
        if (customer == null) {
            model.addAttribute("success", "Delete customer successfully");
        } else {
            model.addAttribute("error", "Delete customer failure, may be error from server");
        }

        return "redirect:/customers";
    }

}
