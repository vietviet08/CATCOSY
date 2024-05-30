package com.dacs1.customer.controller;

import com.dacs1.library.dto.OrderDetailDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;
import com.dacs1.library.model.OrderDetail;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.OrderDetailService;
import com.dacs1.library.service.OrderService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class AccountController {
    @Autowired
    private ServletContext servletContext;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/account")
    public String myAccount(Principal principal, Model model) {

        if (principal == null) return "redirect:/login";
        model.addAttribute("title", "Account");

        Customer customer = customerService.findByUsername(principal.getName());

        model.addAttribute("nameUser", customer.getFirstName() + " " + customer.getLastName());
        model.addAttribute("firstName", customer.getFirstName());
        model.addAttribute("lastName", customer.getLastName());
        model.addAttribute("emailUser", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());
        model.addAttribute("imageAvatar", customer.getImage());

        return "account";
    }


    @PostMapping("/account")
    public String changeInfo(@RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("phone") String phone,
                             @RequestParam("avatarImage") MultipartFile image,
                             Model model,
                             Principal principal) {

        Customer customer = customerService.findByUsername(principal.getName());


        try {

            if (firstName.trim().equals("") || lastName.trim().equals("")) {
                model.addAttribute("notAccept", "First name or last name not empty");
                return "account";
            } else {

                customer.setFirstName(firstName);
                customer.setLastName(lastName);
                customer.setPhone(phone);
                if(!image.isEmpty())
                    customer.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
                customerService.updateCustomer(customer);
                model.addAttribute("success", "Change info successfully!");
                return "redirect:/account";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error from server!");
        }


        return "account";
    }


    @GetMapping("/address")
    public String myAddress(Principal principal, Model model) {

        if (principal == null) return "redirect:/login";
        model.addAttribute("title", "Address");

        Customer customer = customerService.findByUsername(principal.getName());

        model.addAttribute("nameUser", customer.getFirstName() + " " + customer.getLastName());
        model.addAttribute("emailUser", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());

        String addressDetail = customer.getAddressDetail();
        if (addressDetail != null) {
            String[] address = addressDetail.split(" - ");

            if (address.length > 1) {
                String addressSelect = address[1];
                String[] childAddress = addressSelect.split(", ");

                model.addAttribute("citySelect", childAddress[0]);
                model.addAttribute("districtSelect", childAddress[1]);
                model.addAttribute("communeSelect", childAddress[2]);
            }
            model.addAttribute("address", address[0]);
        }

        return "account-address";
    }

    @PostMapping("/address")
    public String saveMyAddress(Principal principal, Model model,
                                @RequestParam("phone") String phone,
                                @RequestParam("addressDetail") String addressDetail,
                                @RequestParam("city") String city,
                                @RequestParam("district") String district,
                                @RequestParam("commune") String commune) {

        if (principal == null) return "redirect:/login";
        Customer customer = customerService.findByUsername(principal.getName());
        try {
            String finishAddress = "";

            if (city.isEmpty()) finishAddress = addressDetail;
            else if (district.isEmpty()) finishAddress = addressDetail + " - " + city;
            else if (commune.isEmpty()) finishAddress = addressDetail + " - " + city + ", " + district;
            else finishAddress = addressDetail + " - " + city + ", " + district + ", " + commune;

            customer.setPhone(phone);
            customer.setAddressDetail(finishAddress);


            customerService.updateCustomer(customer);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "redirect:/address";
    }


    /*Orders*/
    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {

        if (principal == null) return "redirect:/login";
        model.addAttribute("title", "Orders");

        List<Order> orders = orderService.finAllOrderByCustomerId(customerService.findByUsername(principal.getName()));

        model.addAttribute("orders", orders);

        return "account-orders";
    }

//    @GetMapping("/orders/view/{code}")
//    public String viewOrder(@PathVariable("code") String codeViewOrder, Model model) {
//
//        try {
//            Order order = orderService.findOrderByCodeViewOrder(codeViewOrder);
//
//            model.addAttribute("order", order);
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return "404";
//        }
//
//        return "view-orders";
//    }

    @GetMapping("/orders/view/test")
    public String viewOrder(Model model) {
        model.addAttribute("dateNow", new Date());
        model.addAttribute("order", orderService.findOrderById(22L));
        return "view-orders";
    }

    @GetMapping("orders/view/pdf/{code}")
    public ResponseEntity<ByteArrayResource> getPDF(@PathVariable("code") String codeViewOrder,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    Model model) throws IOException {

        // Do Business Logic
        Order order =  orderService.findOrderByCodeViewOrder(codeViewOrder);

        // Create HTML using Thymeleaf template Engine
        Context context = new Context();
        context.setVariable("order", order);
        String orderHtml = templateEngine.process("view-orders", context);

        // Setup Source and target I/O streams
        ByteArrayOutputStream target = new ByteArrayOutputStream();

        // Setup converter properties
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:8086");

        // Call convert method
        HtmlConverter.convertToPdf(orderHtml, target, converterProperties);

        // Extract output as bytes
        byte[] bytes = target.toByteArray();
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

        // Send the response as downloadable PDF
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order.pdf")
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(byteArrayResource);
    }

    @RequestMapping(value = "/cancel-order", method = {RequestMethod.GET, RequestMethod.PUT})
    public String cancelOrder(Order order, Principal principal, Model model) {
        if (principal == null) return "redirect:/404";

        if (order.isAccept()) return "redirect:/orders";

        orderService.cancelOrderForCustomer(order.getId());
        model.addAttribute("success", "Accept order successfully!");

        return "redirect:/orders";
    }

    @RequestMapping(value = "/detail-order", method = {RequestMethod.GET})
    @ResponseBody
    public List<OrderDetailDto> viewOrderDetail(Long id, Model model) {
        return orderDetailService.finAllByOrderIdDto(id);
    }


    @GetMapping("/change-password")
    public String changePassword(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("title", "Orders");

        return "account-password";
    }

    @PostMapping(value = "/do-change-password")
    public String doChangePassword(@RequestParam("oldPassword") String oldPassword,
                                   @RequestParam("password") String password,
                                   @RequestParam("repeatPassword") String repeatPassword,
                                   Model model,
                                   Principal principal) {
        try {

            Customer customer = customerService.findByUsername(principal.getName());
            if (passwordEncoder.matches(oldPassword, customer.getPassword())) {
                if (password.length() <= 3) model.addAttribute("notLength", "Password has at least 4 characters!");
                else if (password.equals(repeatPassword)) {
                    customer.setPassword(passwordEncoder.encode(password));
                    customerService.updateCustomer(customer);
                    model.addAttribute("success", "Change password successfully!");
                } else {
                    model.addAttribute("notSame", "Please enter a matching password!");
                    return "account-password";
                }
            } else {
                model.addAttribute("notCorrect", "Please enter the correct password!");
                return "account-password";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error from server");
        }

        return "account-password";
    }
}
