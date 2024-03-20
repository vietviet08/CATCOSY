package com.dacs1.admin.controller;

import com.dacs1.admin.helper.SetNameAndRoleToPage;
import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Category;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.repository.SizeRepository;
import com.dacs1.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ProductImageService productImageService;


    @GetMapping("/products")
    public String productPage(Model model) {
        List<ProductDto> products = productService.findAllProduct();
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if(!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                images.put(productImage.getProduct().getId(), productImage.getImage());
            }
        }

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.size());
        model.addAttribute("imagesMap", images);
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


    @PostMapping("/save-product")
    public String addProduct(@ModelAttribute("newProduct") ProductDto productDto,
                             @RequestParam("listImage") List<MultipartFile> ListFiles,
                             RedirectAttributes attributes) {

        try {
            productService.save(ListFiles, productDto);
            attributes.addFlashAttribute("success", "Add product successfully!");
        } catch (DataIntegrityViolationException e) {
            attributes.addFlashAttribute("warning", "Name product already exist!");
            e.printStackTrace();
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Add product failed, may be error from server!");
            e.printStackTrace();
        }


        return "redirect:/products";
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
    public String updateProduct(@RequestParam("img1") List<MultipartFile> img1,
                                @ModelAttribute("productUpdate") ProductDto product) {
        try {
            productService.update(img1, product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/products";
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
