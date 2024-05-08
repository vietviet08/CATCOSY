package com.dacs1.admin.controller;

import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.enums.Role;
import com.dacs1.library.model.Admin;
import com.dacs1.library.model.Customer;
import com.dacs1.library.service.AdminService;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.RoleService;
import org.eclipse.angus.mail.imap.protocol.MODSEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;

@Controller
public class EmployeesController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/employees")
    public String employees(Model model) {

        model.addAttribute("title", "Employees");
        model.addAttribute("employees", adminService.findAll());

        model.addAttribute("roles", Role.values());

        model.addAttribute("newEmployee", new Admin());
        return "employees";
    }

    @PostMapping("/new-employee")
    public String addEmployee(@ModelAttribute("newEmployee") Admin admin,
                              @RequestParam("role") String role,
                              RedirectAttributes attributes) {

        try {
            if (role.equals(Role.CUSTOMER.name())) {
                Customer customer = customerService.saveByAdmin(admin);
                if (customer == null) attributes.addFlashAttribute("warning", "Username or email already exist!");
            } else {
                if (adminService.finByUsername(admin.getUsername()) != null)
                    attributes.addFlashAttribute("warning", "Username already exist!");
                else if (adminService.findByEmail(admin.getEmail()) != null)
                    attributes.addFlashAttribute("warning", "Email already exist!");
                else {
                    admin.setRoles(Arrays.asList(roleService.findByRoleName(role)));
                    admin.setPassword(passwordEncoder.encode(admin.getPassword()));
                    adminService.saveEmployee(admin);
                }
            }
            attributes.addFlashAttribute("success", "Add employees successfully!");

        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error from server!");
            e.printStackTrace();

        } finally {
            return "redirect:/employees";
        }

//        return "redirect:/employees";
    }

    @RequestMapping(value = "/findByUsernameEmployee", method = {RequestMethod.GET, RequestMethod.PUT})
    @ResponseBody
    public Admin getEmployee(@RequestParam("username") String username) {
        System.out.println(username);
        return adminService.findByUsername(username);
    }


    @PostMapping("/update-employee")
    public String updateEmployee(@RequestParam("idUpdate") Long id,
                                 @RequestParam("firstNameUpdate") String firstNameUpdate,
                                 @RequestParam("lastNameUpdate") String lastNameUpdate,
                                 @RequestParam("emailUpdate") String emailUpdate,
                                 @RequestParam("phoneUpdate") String phoneUpdate,
                                 @RequestParam("roleUpdate") String roleUpdate,
                                 RedirectAttributes attributes) {
        try {

            AdminDto admin = new AdminDto();
            admin.setFirstName(firstNameUpdate);
            admin.setLastName(lastNameUpdate);
            admin.setEmail(emailUpdate);
            admin.setPhone(phoneUpdate);
            adminService.updateEmployee(admin, id, roleUpdate);
            attributes.addFlashAttribute("success", "Update employee successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Update employee fail, maybe error from server!");
        }

        return "redirect:/employees";
    }

    @GetMapping("/lock-employee")
    public String lockEmployee(Model model,
                               Principal principal,
                               @RequestParam("id") Long id) {

        try {

            adminService.lockEmployee(id, principal);

            model.addAttribute("success", "Employee has been locked!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lock employee error, maybe error from server!");
        }

        return "redirect:/employees";
    }

    @GetMapping("/activate-employee")
    public String activateEmployee(Model model, @RequestParam("id") Long id) {

        try {

            adminService.activateEmployee(id);

            model.addAttribute("success", "Employee has been activated!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Activate employee error, maybe error from server!");
        }

        return "redirect:/employees";
    }

    @GetMapping("/delete-employee")
    public String deleteEmployee(Model model,
                                 Principal principal,
                                 @RequestParam("id") Long id) {

        try {
            adminService.deleteEmployee(id, principal);

            model.addAttribute("success", "Employee has been deleted!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Delete employee error, maybe error from server!");
        }

        return "redirect:/employees";
    }

}
