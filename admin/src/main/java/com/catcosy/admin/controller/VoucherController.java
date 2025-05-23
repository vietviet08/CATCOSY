package com.catcosy.admin.controller;

import com.catcosy.admin.utils.ExcelExporter;
import com.catcosy.library.dto.VoucherDto;
import com.catcosy.library.enums.ObjectManage;
import com.catcosy.library.model.Voucher;
import com.catcosy.library.service.MailService;
import com.catcosy.library.service.VoucherService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private MailService mailService;

    @GetMapping("/export-vouchers")
    public void exportProduct(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=vouchers_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelExporter excelExporter = new ExcelExporter(voucherService.getAllVoucher());

        List<String> fieldsToExport = List.of(
                "id",
                "codeVoucher",
                "price",
                "percentOfTotalPrice",
                "minimumPrice",
                "minimumTotalProduct",
                "usageLimits",
                "expiryDate",
                "forEmailCustomer",
                "isUsed",
                "isActivated");
        excelExporter.export(response, ObjectManage.Vouchers.name(), fieldsToExport);
    }

    @GetMapping("/vouchers")
    public String getAllVoucher(Model model) {

        List<Voucher> vouchers = voucherService.getAllVoucher();

        model.addAttribute("title", "Voucher");
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("size", vouchers.size());
        model.addAttribute("newVoucher", new Voucher());

        return "voucher";
    }

    @RequestMapping(value = "/findByIdVoucher", method = { RequestMethod.GET, RequestMethod.PUT })
    @ResponseBody
    public VoucherDto getVoucher(Long id) {
        VoucherDto voucher = voucherService.getVoucherDtoById(id);
        System.out.println(voucher.toString());
        return voucher;
    }

    @PostMapping("/add-voucher")
    public String saveVoucher(@ModelAttribute("newVoucher") Voucher newVoucher,
            @RequestParam("expiryDate") String expiryDate, RedirectAttributes attributes) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(expiryDate);
            java.sql.Date expiry = new java.sql.Date(date.getTime());
            newVoucher.setExpiryDate(expiry);
            if (!newVoucher.getForEmailCustomer().isBlank()) {
                mailService.sendMailVoucherToCustomer(newVoucher.getForEmailCustomer(), newVoucher);
            }
            voucherService.saveVoucher(newVoucher);
            attributes.addFlashAttribute("success", "Add voucher successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Add voucher failure! Maybe error from server!");
        }

        return "redirect:/vouchers";
    }

    @PostMapping("/update-voucher")
    public String updateVoucher(Voucher voucher, RedirectAttributes attributes) {
        try {
            boolean check = voucherService.checkEmailVoucher(voucher.getId(), voucher.getForEmailCustomer());
            boolean check1 = voucher.getForEmailCustomer().isEmpty();
            if (!check1 && !check) {
                String mess = mailService.sendMailVoucherToCustomer(voucher.getForEmailCustomer(), voucher);
                System.out.println(mess);
                voucherService.updateVoucher(voucher);
            } else
                voucherService.updateVoucher(voucher);
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
    public String sendMailVoucher(Model model, @RequestParam("id") Long id,
            @RequestParam("sendDetailEmailCustomer") String email, RedirectAttributes attributes) {
        try {
            String mess = mailService.sendMailVoucherToCustomer(email, voucherService.getVoucherById(id).get());
            model.addAttribute("message", mess);
            model.addAttribute("success", "Send mail successfully!");

            attributes.addFlashAttribute("success", mess);
            // attributes.addFlashAttribute("success", "Send mail successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Send mail failure! Maybe error from server!");
            model.addAttribute("error", "Send mail failure! Maybe error from server!");
        }

        return "redirect:/vouchers";
    }

}
