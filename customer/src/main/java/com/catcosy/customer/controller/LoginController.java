package com.catcosy.customer.controller;

import com.catcosy.library.dto.CustomerDto;
import com.catcosy.library.enums.Provider;
import com.catcosy.library.model.Customer;
import com.catcosy.library.service.CustomerService;
import com.catcosy.library.service.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.regex.Pattern;

@Controller
public class LoginController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @GetMapping({"/login", "/register"})
    public String loginPage(Model model) {
        model.addAttribute("title", "Login or Register");
        model.addAttribute("customerDto", new CustomerDto());
        return "login_register";
    }

    @PostMapping("/do-register")
    public String doRegister(@Valid @ModelAttribute("customer") CustomerDto customerDto, Model model, BindingResult result) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("message", "Binding has errors ... ");
                model.addAttribute("customerDto", customerDto);
                return "login_register";
            }

//            String date = customerDto.getBirthDay();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String birthDate = sdf.format(date);
//            System.out.println(birthDate);

            Customer customer = customerService.findByUsername(customerDto.getUsername());
            if (customer == null) {
                if (customerDto.getPassword().equals(customerDto.getPasswordRepeat())) {
                    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
                    Pattern pattern = Pattern.compile(emailRegex);
                    if (pattern.matcher(customerDto.getEmail()).matches()) {
                        CustomerDto customer1 = customerService.findByEmailAndProvider(customerDto.getEmail(), Provider.local.name());
                        if (customer1 == null) {

                            customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                            customerService.save(customerDto);
                            model.addAttribute("customerDto", customerDto);
                            model.addAttribute("success", "Register successfully!");

                            mailService.sendMailToCustomer(customerDto);

                            return "login_register";
                        } else {
                            model.addAttribute("customerDto", customerDto);
                            model.addAttribute("message", "Email registered, please choose another email!");
                            return "login_register";
                        }
                    } else {
                        model.addAttribute("customerDto", customerDto);
                        model.addAttribute("message", "Please enter email in correct format!");
                        return "login_register";
                    }
                } else {
                    model.addAttribute("customerDto", customerDto);
                    model.addAttribute("message", "Password isn't correct!");
                    return "login_register";
                }

            } else {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("message", "Username had exist!");
                return "login_register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error from server!");
        }

        return "login_register";
    }


}
