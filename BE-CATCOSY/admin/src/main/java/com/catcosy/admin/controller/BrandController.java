package com.catcosy.admin.controller;

import com.catcosy.admin.utils.ExcelExporter;
import com.catcosy.library.enums.ObjectManage;
import com.catcosy.library.model.Brand;
import com.catcosy.library.service.BrandService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/brands")
    public String brandsPage(Model model) {
        List<Brand> brands = brandService.findAllBrand();
        model.addAttribute("brands", brands);
        model.addAttribute("size", brands.size());
        model.addAttribute("title", "Brands");
        model.addAttribute("newBrand", new Brand());
        return "brands";
    }

    @GetMapping("/export-brands")
    public void exportBrands(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=brands_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelExporter<Brand> excelExporter = new ExcelExporter<>(brandService.findAllBrand());

        List<String> fieldsToExport = List.of("id",
                "name",
                "isDeleted",
                "isActivated");
        excelExporter.export(response, ObjectManage.Brands.name(), fieldsToExport);
    }

    @PostMapping("/add-brand")
    public String addBrand(@ModelAttribute("newBrand") Brand brand, RedirectAttributes attributes) {
        try {
            brandService.save(brand);
            attributes.addFlashAttribute("success", "Add brand successfully!");
        } catch (DataIntegrityViolationException e) {
            attributes.addFlashAttribute("warning", "Name brand already exist!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error from service, contact to admin system to fix this!");
        }
        return "redirect:/brands";
    }

    @RequestMapping(value = "/delete-brand", method = { RequestMethod.GET, RequestMethod.PUT })
    public String deleteBrand(Brand brand, RedirectAttributes attributes) {
        try {
            brandService.deleteById(brand.getId());
            attributes.addFlashAttribute("success", "Deleted brand!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Delete brand failed!");
        }
        return "redirect:/brands";
    }

    @RequestMapping(value = "/activate-brand", method = { RequestMethod.GET, RequestMethod.PUT })
    public String activateBrands(Brand brand, RedirectAttributes attributes) {
        try {
            brandService.activatedById(brand.getId());
            attributes.addFlashAttribute("success", "Activated brand!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Activate brand failed!");
        }
        return "redirect:/brands";
    }

    @RequestMapping(value = "/findByIdBrand", method = { RequestMethod.PUT, RequestMethod.GET })
    @ResponseBody
    public Optional<Brand> findBrandById(Long id) {
        return brandService.findById(id);
    }

    @PostMapping("/update-brand")
    public String updateBrand(Brand brand, RedirectAttributes attributes) {
        try {
            brandService.update(brand);
            attributes.addFlashAttribute("success", "Update brand successfully!");
        } catch (DataIntegrityViolationException e1) {
            attributes.addFlashAttribute("error", "Update brand not accept, name brand exist!");
            e1.printStackTrace();
        } catch (Exception e2) {
            attributes.addFlashAttribute("failed", "Update brand failed!");
            e2.printStackTrace();
        }
        return "redirect:/brands";
    }

}
