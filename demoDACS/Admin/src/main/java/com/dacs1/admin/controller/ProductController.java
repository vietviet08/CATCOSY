package com.dacs1.admin.controller;

import com.dacs1.admin.helper.SetNameAndRoleToPage;
import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Category;
import com.dacs1.library.model.Product;
import com.dacs1.library.repository.SizeRepository;
import com.dacs1.library.service.AdminService;
import com.dacs1.library.service.CategoryService;
import com.dacs1.library.service.ProductService;
import com.dacs1.library.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SizeService sizeService;


    @GetMapping("/products")
    public String productPage(Model model) {
        List<ProductDto> products = productService.findAllProduct();
        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.size());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "products", adminService);
        return "products";
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        model.addAttribute("title", "Add product");
        model.addAttribute("sizes", sizeService.findAllSize());
        model.addAttribute("categories", categoryService.findAllCategory());
        model.addAttribute("newProduct", new ProductDto());
//        SetNameAndRoleToPage.setNameAndRoleToPage(model, "add-product", adminService);
        return "add-product";
    }


    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute("newProduct") ProductDto productDto,
                             @RequestParam("img1") MultipartFile img1,
                             @RequestParam("img2") MultipartFile img2,
                             @RequestParam("img3") MultipartFile img3,
                             @RequestParam("img4") MultipartFile img4,
                             RedirectAttributes attributes) {

        try {
            productService.save(img1, img2, img3, img4, productDto);
            attributes.addFlashAttribute("success", "Add product successfully!");
        } catch (DataIntegrityViolationException e) {
            attributes.addFlashAttribute("warning", "Name product already exist!");
            e.printStackTrace();
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Add product failed, may be error from server!");
            e.printStackTrace();
        }


        return "redirect:/products/0";
    }

    @GetMapping("/update-product/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model) {
        ProductDto productDto = productService.findById(id);
        List<Category> categoryList = categoryService.findAllCategoryIsActivate();
        model.addAttribute("title", "Update product");
        model.addAttribute("product", productDto);
        model.addAttribute("sizes", sizeService.findAllSize());
        model.addAttribute("categories", categoryList);
        return "update-product";
    }


    @RequestMapping("/update-product/{id}")
    public String updateProduct(@RequestParam("img1") MultipartFile img1,
                                @RequestParam("img2") MultipartFile img2,
                                @RequestParam("img3") MultipartFile img3,
                                @RequestParam("img4") MultipartFile img4,
                                @ModelAttribute("productUpdate") ProductDto product) {
        try {
            productService.update(img1, img2, img3, img4, product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/delete-product", method = {RequestMethod.GET, RequestMethod.PUT})
    public String deleteProduct(ProductDto product) {
        productService.deleteById(product.getId());
        return "redirect:/products";
    }


    @RequestMapping(value = "/activate-product", method = {RequestMethod.GET, RequestMethod.PUT})
    public String activateProduct(ProductDto product) {
        productService.activateById(product.getId());
        return "redirect:/products";
    }


}
