package com.dacs1.admin.controller;

import com.dacs1.admin.helper.SetNameAndRoleToPage;
import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Category;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.model.Size;
import com.dacs1.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

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
    public String productPage(@RequestParam(required = false, defaultValue = "0") Integer page,
                              @RequestParam(required = false) String search,
                              Model model) {
        Page<ProductDto> products = null;
        if (search != null && !search.isEmpty())
            products = productService.pageProductSearch(search, page, 10);
        else if (page == null) {
            products = productService.pageProduct(0, 10);
            page = 0;

        } else {
            products = productService.pageProduct(page, 10);
        }
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                images.put(productImage.getProduct().getId(), productImage.getImage());
            }
        }
        System.out.println(productImages.size());
        System.out.println(images.size());

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("imagesMap", images);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
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
                             @RequestParam("sizesChoose") String[] sizesSelected,
                             RedirectAttributes attributes) {
        List<Long> selectedSizeIds = new ArrayList<>();

        for (String n : sizesSelected){
            selectedSizeIds.add(Long.parseLong(n));
            System.out.println(n);
        }

        try {
            productService.save(ListFiles, selectedSizeIds, productDto);
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
    public ProductDto getById(Long id) {
        return productService.getById(id);
    }

    @GetMapping("/update-product/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model) {
        ProductDto productDto = productService.getById(id);
        List<Category> categoryList = categoryService.findAllCategoryIsActivate();
        model.addAttribute("title", "Update product");
        model.addAttribute("productDto", productDto);
        model.addAttribute("sizes", sizeService.findAllSize());
        model.addAttribute("sizesExisting", productDto.getSizes());
        model.addAttribute("categories", categoryList);
        return "update-product";
    }


    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto, @RequestParam("listImage") List<MultipartFile> images, RedirectAttributes attributes) {
        try {
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
        return "redirect:/products/0";
    }


    @RequestMapping(value = "/activate-product", method = {RequestMethod.GET, RequestMethod.PUT})
    public String activateProduct(ProductDto product) {
        productService.activateById(product.getId());
        return "redirect:/products/0";
    }


    @GetMapping("/products/{pageNo}")
    public String pageProduct(@PathVariable("pageNo") int pageNo, Model model) {

        Page<ProductDto> products = productService.pageProduct(pageNo, 10);
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

        Page<ProductDto> products = productService.pageProductSearch(key, pageNo, 10);
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


    @GetMapping("/sort-prices")
    public String sortPrice(@RequestParam("nameOption") int nameOption, Model model) {
//        switch (nameOption) {
//            case 1: {
//                model.addAttribute("productSortedDesc", productService.sortDesc());
//            }
//            case 2:
//                model.addAttribute("productSortedAsc", productService.sortAsc());
//        }
        return "products";
    }

    @GetMapping("/sort-category")
    public List<ProductDto> sortCategory(@RequestParam("nameOption") String nameOption) {
        return productService.byCategory(nameOption);
    }


}
