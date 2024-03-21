package com.dacs1.admin.controller;

import com.dacs1.admin.helper.SetNameAndRoleToPage;
import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Category;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if (!productImages.isEmpty()) {
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


        return "redirect:/products/0";
    }

    @RequestMapping(value = "/findByIdProduct", method = {RequestMethod.GET, RequestMethod.PUT})
    @ResponseBody
    public Optional<Product> getById(Long id) {
        return productService.getById(id);
    }

    @GetMapping("/update-product/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model) {
        ProductDto productDto = productService.findById(id);
        List<Category> categoryList = categoryService.findAllCategoryIsActivate();
        model.addAttribute("title", "Update product");
        model.addAttribute("productUpdate", productDto);
        model.addAttribute("sizes", sizeService.findAllSize());
        model.addAttribute("sizesExisting", productDto.getSizes());
        model.addAttribute("categories", categoryList);
        return "update-product";
    }


    @PostMapping(value = "/update-product/{id}")
    public String updateProduct(@ModelAttribute("productUpdate") ProductDto productDto,
                                @RequestParam("listImage") List<MultipartFile> images,
                                RedirectAttributes attributes) {
        try {
//            System.out.println(productDto.toString());
            productService.update(images, productDto);
            productService.updateProductSize(productDto.getId(), productDto.getSizes());
            attributes.addFlashAttribute("success", "Update product successfully!");

        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Update product failed!");
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


    @GetMapping("/products/{pageNo}")
    public String pageProduct(@PathVariable("pageNo") int pageNo, Model model) {

        Page<ProductDto> products = productService.pageProduct(pageNo);
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                images.put(productImage.getProduct().getId(), productImage.getImage());
            }
        }

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("imagesMap", images);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", products.getTotalPages());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "products", adminService);
        return "products";
    }

    @GetMapping("/search-products/{pageNo}")
    public String pageProductSearch(@PathVariable("pageNo") int pageNo, @RequestParam("keyword") String key, Model model) {

        Page<ProductDto> products = productService.pageProductSearch(key, pageNo);
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                images.put(productImage.getProduct().getId(), productImage.getImage());
            }
        }

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("imagesMap", images);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", products.getTotalPages());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "products", adminService);
        return "products";
    }


}
