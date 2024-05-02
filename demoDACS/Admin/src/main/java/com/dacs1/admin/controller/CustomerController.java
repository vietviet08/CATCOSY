package com.dacs1.admin.controller;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.repository.CustomerRepository;
import com.dacs1.library.service.CustomerService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String unlockCustomer(@RequestParam("usernameCustomer") String username, Model model){
        customerService.unlockCustomer(username);
        model.addAttribute("success", "Unlock customer successfully");
        return "redirect:/customers";
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@RequestParam("usernameCustomerNeedDelete") String usernameCustomerNeed, Model model) {
       Customer customer = customerService.deleteCustomer(usernameCustomerNeed);
       if(customer == null) {
           model.addAttribute("success", "Delete customer successfully");
       }else{
           model.addAttribute("error", "Delete customer failure, may be error from server");
       }

        return "redirect:/customers";
    }

}
