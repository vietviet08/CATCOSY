package com.dacs1.admin.controller;

import com.dacs1.library.model.Voucher;
import com.dacs1.library.service.MailService;
import com.dacs1.library.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private MailService mailService;

    @GetMapping("/vouchers")
    public String getAllVoucher(Model model) {

        List<Voucher> vouchers = voucherService.getAllVoucher();

        model.addAttribute("title", "Voucher");
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("size", vouchers.size());
        model.addAttribute("newVoucher", new Voucher());


        return "voucher";
    }

    @RequestMapping(value = "/findByIdVoucher", method = {RequestMethod.GET, RequestMethod.PUT})
    @ResponseBody
    public Voucher getVoucher(Long id) {
        return voucherService.getVoucherById(id).get();
    }

    @PostMapping("/add-voucher")
    public String saveVoucher(@ModelAttribute("newVoucher") Voucher newVoucher, @RequestParam("expiryDate") String expiryDate, RedirectAttributes  attributes) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(expiryDate);
            java.sql.Date expiry = new java.sql.Date(date.getTime());
            newVoucher.setExpiryDate(expiry);
            voucherService.saveVoucher(newVoucher);
            attributes.addFlashAttribute("success", "Add voucher successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Add voucher failure! Maybe error from server!");
        }

        return "redirect:/vouchers";
    }

    @PostMapping("update-voucher")
    public String updateVoucher(RedirectAttributes attributes) {

        try {

            attributes.addFlashAttribute("success", "Update voucher successfully");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Update voucher failure! Maybe error from server!");
        }


        return "redirect:/vouchers";
    }

    @GetMapping("/delete-voucher")
    public String deleteVoucher(@RequestParam("id") Long id, RedirectAttributes attributes) {

        try {
            voucherService.deleteVoucher(id);
            attributes.addFlashAttribute("success", "Delete voucher successfully");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Delete voucher failure! Maybe error from server!");
        }


        return "redirect:/vouchers";
    }

    @GetMapping("activate-voucher")
    public String activateVoucher(@RequestParam("id") Long id, RedirectAttributes attributes) {
        try {
            voucherService.activateVoucher(id);
            attributes.addFlashAttribute("success", "Activate voucher successfully");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Activate voucher failure! Maybe error from server!");
        }

        return "redirect:/vouchers";
    }


    @PostMapping("/send-mail-voucher")
    public String sendMailVoucher(Model model, @RequestParam("id") Long id, @RequestParam("sendDetailEmailCustomer") String email, RedirectAttributes attributes) {
        try {
          String mess =  mailService.sendMailVoucherToCustomer(email, voucherService.getVoucherById(id).get());
          model.addAttribute("message", mess);
          model.addAttribute("success", "Send mail successfully!");

          attributes.addFlashAttribute("message", mess);
//          attributes.addFlashAttribute("success", "Send mail successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error","Send mail failure! Maybe error from server!" );
            model.addAttribute("error", "Send mail failure! Maybe error from server!");
        }

        return "redirect:/vouchers";
    }

}
