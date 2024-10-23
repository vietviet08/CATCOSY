package com.catcosy.admin.controller;

import com.catcosy.admin.helper.SetNameAndRoleToPage;
import com.catcosy.admin.utils.ExcelExporter;
import com.catcosy.library.enums.ObjectManage;
import com.catcosy.library.model.Category;
import com.catcosy.library.service.AdminService;
import com.catcosy.library.service.CategoryService;
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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/categories")
    public String categoriesPage(Model model) {
        List<Category> categoryList = categoryService.findAllCategory();
        model.addAttribute("categories", categoryList);
        model.addAttribute("size", categoryList.size());
        model.addAttribute("title", "Categories");
        model.addAttribute("newCategory", new Category());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "categories", adminService);
        return "categories";
    }

    @GetMapping("/export-categories")
    public void exportCategories(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=categories_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelExporter<Category> excelExporter = new ExcelExporter<>(categoryService.findAllCategory());

        List<String> fieldsToExport = List.of("id",
                "name",
                "isDeleted",
                "isActivated");
        excelExporter.export(response, ObjectManage.Categories.name(), fieldsToExport);
    }

    @PostMapping("/add-category")
    public String addCategory(@ModelAttribute("newCategory") Category category, RedirectAttributes attributes) {
        try {
            categoryService.save(category);
            attributes.addFlashAttribute("success", "Add category successfully!");
        } catch (DataIntegrityViolationException e) {
            attributes.addFlashAttribute("warning", "Name category already exist!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error from service, contact to admin system to fix this!");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "/delete-category", method = { RequestMethod.GET, RequestMethod.PUT })
    public String deleteCategory(Category category, RedirectAttributes attributes) {
        try {
            categoryService.deleteById(category.getId());
            attributes.addFlashAttribute("success", "Deleted category!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Delete category failed!");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "/activate-category", method = { RequestMethod.GET, RequestMethod.PUT })
    public String activateCategory(Category category, RedirectAttributes attributes) {
        try {
            categoryService.activatedById(category.getId());
            attributes.addFlashAttribute("success", "Activated category!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Activate category failed!");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "findById", method = { RequestMethod.PUT, RequestMethod.GET })
    @ResponseBody
    public Optional<Category> findCategoryById(Long id) {
        return categoryService.findById(id);
    }

    @GetMapping("/update-category")
    public String updateCategory(Category category, RedirectAttributes attributes) {
        try {
            categoryService.update(category);
            attributes.addFlashAttribute("success", "Update category successfully!");
        } catch (DataIntegrityViolationException e1) {
            attributes.addFlashAttribute("error", "Update category not accept, name category exist!");
            e1.printStackTrace();
        } catch (Exception e2) {
            attributes.addFlashAttribute("failed", "Update category failed!");
            e2.printStackTrace();
        }
        return "redirect:/categories";
    }

}
